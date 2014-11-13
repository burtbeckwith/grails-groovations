<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>Groovy Migrations</title>
</head>
<body>
    <div>
        <div>
            <g:if test="${areMigrationsExecuting}">
                Executing Migrations:
            </g:if>
            <g:else>
                Pending Migrations: <g:link action="executePendingMigrations">Execute</g:link>
            </g:else>
        </div>
        <g:render template="migrationsTable" model="[migrations: pendingMigrations]"/>
        <br/>
        <div>
            Executed Migrations:
        </div>
        <g:render template="migrationsTable" model="[migrations: executedMigrations]"/>
        <g:paginate total="${numExecutedMigrations}" next="Next" prev="Previous" max="${pageSize}"/>
    </div>
</body>
</html>