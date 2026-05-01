<?php

namespace App\Http\Controllers;

use App\Models\Inspection;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Log;

class InspectionController extends Controller
{
    public function index()
    {
        $inspections = Inspection::orderBy('captured_at', 'desc')->get();
        return view('inspections.index', compact('inspections'));
    }

    public function receive(Request $request)
    {
        // 1. Capture the raw input
        $items = $request->json()->all();

        Log::info("SYNC START: Received " . count($items) . " items from phone.");

        if (empty($items)) {
            Log::warning("SYNC WARNING: Payload was empty!");
            return response()->json(['status' => 'empty'], 200);
        }

        // 2. Process items
        foreach ($items as $item) {
            try {
                Inspection::updateOrCreate(
                    ['id' => $item['id']],
                    [
                        'job_id' => $item['jobId'] ?? 'unknown',
                        'title'  => $item['title'] ?? 'No Title',
                        'result' => $item['result'] ?? 'Pending',
                        'captured_at' => now(),
                    ]
                );
                Log::info("SYNC SUCCESS: Saved item " . $item['id']);
            } catch (\Exception $e) {
                Log::error("SYNC ERROR on item " . ($item['id'] ?? 'unknown') . ": " . $e->getMessage());
            }
        }

        return response()->json([
            'status' => 'success',
            'received' => count($items)
        ], 200);
    }
}
