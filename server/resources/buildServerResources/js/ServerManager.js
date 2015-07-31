/**
 * Created by sbt-voroshilov-ss on 27.07.2015.
 */
function ServerManagerConstructor(controllerPath) {
    this.controllerPath = controllerPath;
}

ServerManagerConstructor.prototype.generateURL = function (action, serverName, compName) {
    var url = this.controllerPath;
    if (action != undefined)
        url += "?action=" + action;
    if (serverName != undefined)
        url += "&server=" + encodeURIComponent(serverName);
    if (compName != undefined)
        url += "&compName=" + encodeURIComponent(compName);
    return url;
};

ServerManagerConstructor.prototype.killComp = function (serverName, compName) {
    var that = this;
    //var popup = $j('<div>').attr("class","srvrmgr-popup").text("HAHAHAHAHAHHAHHAHAHHAHAHAHHAHAHHAHAHHAH");
    //$j("body").appendChild(popup);
    //BS.Util.show($("HAHAHHAHAHHAHHA"));
    $j.get(this.generateURL("killComp", serverName, compName), function (data) {
        //popup.remove();
        that.updateRowClass(data, "CP_DISP_RUN_STATE");
    })
};

ServerManagerConstructor.prototype.startComp = function (serverName, compName) {
    var that = this;
    $j.get(this.generateURL("startComp", serverName, compName), function (data) {
        that.updateRowClass(data, "CP_DISP_RUN_STATE");
    })
};

ServerManagerConstructor.prototype.refreshCompState = function (serverName) {
    var that = this;
    $j.get(this.generateURL("refreshCompState", serverName), function (data) {
        that.updateRowClass(data, "CP_DISP_RUN_STATE");
    });
    setTimeout(function () {
        that.refreshCompState(serverName);
    }, 5000);
};

ServerManagerConstructor.prototype.updateRowClass = function (data, rowName) {

    var className = "";
    var nativeClass = "name highlight";

    for (var it in data) {
        if (data.hasOwnProperty(it)) {
            className = "siebel-comp-" + data[it].toLowerCase().replace(/\s/g, "-");
            $j("tr[data-comp-alias='" + it + "'] td[data-param-name='" + rowName + "']").attr("class", nativeClass + " " + className).text(data[it]);
        }
    }
};

//ServerManager = new ServerManagerConstructor();