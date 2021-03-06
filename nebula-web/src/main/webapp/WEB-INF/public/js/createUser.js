$(function(){
    showSelect2();
    $("#insertform").validate({
        rules:{
            username:{required:true},
            mobilePhone:{required:true},
            weixinAcc:{required:true},
            //qqAcc:{required:true},
            //email:{required:true},
            nickname:{required:true},
            deptName:{required:true},
            bu:{required:true},
            jobTitle:{required:true},
            empId:{required:true},
            supervisorEmpId:{required:true},
            roleList:{required:true},
            isEnable:{required:true},
            password:{required:true},
        },
        messages:{
            username:{required:"用户名不能为空"},
            mobilePhone:{required:"手机号码不能为空"},
            //weixinAcc:{required:"微信账号不能为空"},
            //qqAcc:{required:"qq账号不能为空"},
            email:{required:"邮箱不能为空"},
            nickname:{required:"昵称不能为空"},
            deptName:{required:"部门名称不能为空"},
            bu:{required:"事业部不能为空"},
            jobTitle:{required:"职位不能为空"},
            empId:{required:"工号不能为空"},
            supervisorEmpId:{required:"主管工号不能为空"},
            roleList:{required:"角色不能为空"},
            isEnable:{required:"请选择是否启用"},
            password:{required:"密码不能为空"}
        }
    });
    if($("#updatePass")){
        $("#updatePass").hide();
    }
    $("#save").hide();
    //为编辑页面时
    if($("#isEdit").val()!=""){
        $("#password").hide();
        //$("#password_div").append("<button id='updatePass' type='button' class='btn btn-info'>修改密码</button>");
        if($("#updatePass")) {
            $("#updatePass").show();
            $("#updatePass").click(function () {
                $("#password").show();
                if ($("#updatePass").text() == "修改密码") {
                    $("#updatePass").text("保存");
                }
                else {
                    $("#updatePass").text("修改密码");
                    $("#password").hide();
                    $.ajax({
                        async: false,
                        type: "post",
                        data: {
                            "userId": $("#id").val(),
                            "newPassword": $("#password").val()
                        },
                        url: "/user/update/password",
                        datatype: "json",
                        success: function (data) {
                            var msg = "修改密码成功"
                            nebula.common.alert.success(msg, 1000);
                        },
                        error: function (XMLHttpRequest, textStatus, errorThrown) {
                            var msg = "很抱歉修改密码失败，原因" + errorThrown
                            nebula.common.alert.danger(msg, 1000);
                        }
                    });
                }
            });
        }
        $.ajax({
            async: false,
            type:"post",
            data:{"empId":$("#empId").val()},
            url:"/user/get/empId",
            datatype:"json",
            success: function (data) {
                $("#username").val(data["username"]);
                $("#username").attr('disabled',true);
                $("#mobilePhone").val(data["mobilePhone"]);
                $("#weixinAcc").val(data["weixinAcc"]);
                $("#qqAcc").val(data["qqAcc"]);
                $("#email").val(data["email"]);
                $("#nickname").val(data["nickname"]);
                $("#deptName").val(data["deptName"]);
                $("#bu").val(data["bu"]);
                $("#jobTitle").val(data["jobTitle"]);
                $("#empId").val(data["empId"]);
                $("#empId").attr('disabled',true);
                $("#supervisorEmpId").val(data["supervisorEmpId"]);
                $("#isEnable").val(data["isEnable"]);
                //$("#password").val(data["password"]);
                if(data["isEnable"]==1) {
                    $("#enableRadio").attr("checked", true);
                }
                else
                    $("#unenableRadio").attr("checked", true);
                if(data["gIsVerify"]){
                    $("#unbundling_div").append(" <label for='password' class='col-sm-2 control-label'>动态验证码</label> <div class='col-sm-10' style='width: 300px;'> <button type='button' id='unbundling_btn' class='btn btn-warning'>解绑</button> </div>")
                }
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                nebula.common.alert.danger("很抱歉载入角色信息失败，原因"+ errorThrown, 1000);
            }
        });
        $("#submit").hide();
        $("#save").show();
    }

    //提交按钮
    $("#submit").click(function(){
        if($("#insertform").valid()) {
            btnClick(true);
        }
    });
    $("#save").click(function(){
        if($("#insertform").valid()) {
            btnClick(false);
        }
    });
    $("#unbundling_btn").click(function () {
        var ms = confirm("确认解绑么？");
        if (ms == true) {
            $.ajax({
                type:"POST",
                data:{
                    empId: $("#empId").val(),
                },
                url:"/user/unBindingCode",
                success: function (data) {
                    if(data.callbackMsg=="Error") {
                        nebula.common.alert.danger(data.responseContext, 1000);
                        return;
                    }
                    nebula.common.alert.success(data.responseContext, 1000);
                    setTimeout(function () {
                        window.location.href='/user/list.htm';
                    }, 1000);
                },
                error: function (errorThrown) {
                    nebula.common.alert.danger("很抱歉解除动态验证码绑定失败，原因"+ errorThrown, 1000);
                }
            });
        }
    })
})

//角色信息显示select2
function showSelect2(){
    var selectData;
    $.ajax({
        async: false,
        type:"post",
        data:{
            "empId":$("#empId").val()
        },
        url:"/role/selectRole",
        datatype:"json",
        success: function (data) {
            selectData=data;
            $(".js-example-tags").select2({data:selectData},{
                tags: true
            })
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            nebula.common.alert.danger("很抱歉载入角色失败，原因"+ errorThrown, 1000);
        }
    });
}


//新增，修改按钮点击事件
function btnClick(isCreate) {

    var isEnable = $('#isEnable input[name="isEnable"]:checked ').val();
    var id=$("#id").val();
    var username = $("#username").val();
    var mobilePhone = $("#mobilePhone").val();
    var weixinAcc = $("#weixinAcc").val();
    var qqAcc = $("#qqAcc").val();
    var email = $("#email").val();
    var nickname = $("#nickname").val();
    var deptName = $("#deptName").val();
    var bu = $("#bu").val();
    var jobTitle = $("#jobTitle").val();
    var empId = $("#empId").val();
    var supervisorEmpId = $("#supervisorEmpId").val();
    var password = $("#password").val();
    var url, tips;
    if (isCreate) {
        url = "/user/add";
        tips = "新增";
    }
    else {
        url = "/user/update";
        tips = "修改";
    }
    var roleList=$("#roleList").find("option:selected");
    var roleIds=[];
    for(var i=0;i<roleList.length;i++){
        roleIds.push(roleList[i].value);
    }
    $.ajax({
        type: "post",
        url: url,
        data: {
            "id":id,
            "roleIds": roleIds,
            "isEnable": isEnable,
            "username": username,
            "weixinAcc": weixinAcc,
            "qqAcc": qqAcc,
            "mobilePhone":mobilePhone,
            "email": email,
            "nickname": nickname,
            "deptName": deptName,
            "bu": bu,
            "jobTitle": jobTitle,
            "empId": empId,
            "supervisorEmpId": supervisorEmpId,
            "password": password
        },
        datatype: "json",
        success: function (data) {
            if(data.callbackMsg=="Success") {
                var msg = tips + "用户成功";
                nebula.common.alert.success(msg, 1000);
            }
            else{
                var msg = data.responseContext;
                nebula.common.alert.warning(msg, 1000);
            }
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            var msg = "很抱歉" + tips + "用户失败，原因" + errorThrown;
            nebula.common.alert.danger(msg, 1000);
        }
    })
}