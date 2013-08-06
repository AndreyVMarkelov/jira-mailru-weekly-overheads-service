jQuery(document).ready(function() {
    jQuery("#ov-overhead-user-selector").change(function() {
        mailOverheadsGetOverheadData();
    });
});

function mailOverheadsGetBaseUrl() {
    return location.protocol + "//" + location.hostname + (location.port && ":" + location.port) + contextPath;
}

function mailOverheadsGetOverheadData() {
    jQuery.ajax({
        url : mailOverheadsGetBaseUrl() + "/rest/overheadvalssrv/1.0/overheadvalssrv/getoverheaddata",
        type : "GET",
        data : {
            "username" : jQuery("#ov-overhead-user-selector").find("option:selected").val()
        },
        async : true,
        error : function(xhr, ajaxOptions, thrownError) {
            handleError(xhr, ajaxOptions, thrownError);
        },
        success : function(ret_data) {
            if (ret_data) {
                jQuery("#ov-overhead-time-displayer").html(ret_data.time);
                jQuery("#ov-overhead-qa-displayer").html(ret_data.qa);
            }
        }
    });
}