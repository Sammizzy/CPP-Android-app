<?php

use App\Http\Controllers\InspectionController;
use Illuminate\Support\Facades\Route;

// This will automatically call the index() method in your controller
Route::get('/', [InspectionController::class, 'index']);



use App\Http\Controllers\Api\SyncInspectionController;

Route::post('/sync/inspections', [SyncInspectionController::class, 'receive']);

