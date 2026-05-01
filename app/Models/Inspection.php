<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Inspection extends Model
{
    // 1. Allow these fields to be saved (Mass Assignment)
    protected $fillable = ['id', 'job_id', 'title', 'result', 'captured_at'];

    // 2. Tell Laravel the ID is a String (UUID), not an auto-incrementing number
    public $incrementing = false;
    protected $keyType = 'string';
}
