<?php

use App\Http\Controllers\InspectionController;
use Illuminate\Support\Facades\Route;

Route::post('/sync/inspections', [InspectionController::class, 'receive']);

