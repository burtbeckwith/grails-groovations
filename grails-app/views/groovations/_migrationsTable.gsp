<table>
    <thead>
    <tr>
        <th>Resource Path</th>
        <th>Execution Date</th>
    </tr>
    </thead>
    <tbody>
        <g:each var="migration" in="${migrations}">
            <tr>
                <td>${migration.resourcePath}</td>
                <td>${migration.executionDate ?: 'Never executed'}</td>
            </tr>
        </g:each>
    </tbody>
</table>