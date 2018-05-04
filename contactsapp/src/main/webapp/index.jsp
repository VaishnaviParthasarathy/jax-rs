<html>


<head>
    <!-- Bootstrap -->
    <link href="//netdna.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>


<h2>Hello World!</h2>
<div class="container">
    <div class="row">
        <div class="form-group">
            <header class="page-header">
                <h1>Enter your Client Id and Secret:</h1>
            </header>

            <form class="form-group" method="get" action="webapi/setup">
                <div class="form-group">
                    <input class="form-control input-lg" type="text" placeholder="Client Id" name="clientId"
                           autofocus/>
                </div>
                <div class="form-group">
                    <input class="form-control input-lg" type="text" placeholder="Client Secret" name="clientSecret"
                    />
                </div>
                <div class="form-group">
                    <!--<span class="input-group-btn">-->
                    <button class="btn btn-default btn-lg" type="submit">OK</button>
                    <!--</span>-->
                </div>
            </form>
        </div>
    </div>
</div>

<script src="lib/bootstrap.js"></script>

</body>
</html>
