<table class="table">
    <thead>
    <tr>
        <th>Job ID</th>
        <th>Inspection Title</th>
        <th>Result</th>
        <th>Sync Timestamp</th>
    </tr>
    </thead>
    <tbody>
    @foreach($inspections as $entry)
        <tr>
            <td>{{ $entry->job_id }}</td>
            <td>{{ $entry->title }}</td>
            <td>
                <span class="badge {{ $entry->result == 'Pass' ? 'bg-success' : 'bg-warning' }}">
                    {{ $entry->result }}
                </span>
            </td>
            <td>{{ $entry->captured_at }}</td>
        </tr>
    @endforeach
    </tbody>
</table>
