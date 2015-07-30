/**
 * Created by sbt-voroshilov-ss on 27.07.2015.
 */
function ServerManagerConstructor() {
}

ServerManagerConstructor.prototype.killComp = function (controllerPath, compName, serverName) {
    var that = this;
    var url = controllerPath + "?action=killComp&compName=" + encodeURIComponent(compName) + "&server=" + encodeURIComponent(serverName);
    $j.get(url, function (data) {
        if (data["status"] != "Success")
            setTimeout(function () {
                that.AjaxRequest(url);
            }, 2000);
    })
};

ServerManagerConstructor.prototype.getCompState = function (controllerPath, compName, serverName) {
    var that = this;
    var url = controllerPath + "?action=getCompState&compName=" + encodeURIComponent(compName) + "&server=" + encodeURIComponent(serverName);
    $j.get(url, function (data) {
        return data["state"];
    });
};

ServerManagerConstructor.prototype.refreshCompState = function (controllerPath, serverName) {
    var url = controllerPath + "?action=refreshCompState&server=" + encodeURIComponent(serverName);
    var className = "";
    var nativeClass = "name highlight";
    var that = this;
    $j.get(url, function (data) {
        for (var it in data) {
            if(data.hasOwnProperty(it))
            {
                //if(data[it] == "Starting Up")
                //    className = "siebel-comp-starting-up";
                //else if(data[it] == "Online")
                //    className = "siebel-comp-online";
                //else if (data[it] == "Running")
                //    className = "siebel-comp-running";
                //else if(data[it] == "Offline")
                //    className = "siebel-comp-offline";
                //else if(data[it] == "Shutting down")
                //    className = "siebel-comp-shutting-down";
                //else if(data[it] == "Shutdown")
                //    className = "siebel-comp-shutdown";
                //else

                className = "siebel-comp-" + data[it].toLowerCase().replace(/\s/g, "-");

                $j("tr[data-comp-alias='" + it + "'] td[data-param-name='CP_DISP_RUN_STATE']").attr("class", nativeClass + " " + className).text(data[it]);
            }
        }
    });
    setTimeout(function(){
        that.refreshCompState(controllerPath, serverName);
    }, 5000);
};

ServerManager = new ServerManagerConstructor();