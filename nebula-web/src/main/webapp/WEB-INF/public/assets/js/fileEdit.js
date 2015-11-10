$(document).ready(function(){
    //关闭当前页面
    $("#btn_close").click(function(){
        window.close();
    });
    //生成jstree
    $('#tree-div')
        .on("changed.jstree", function (e, data) {
            if(data.selected.length) {
                var selectedObj = data.instance.get_node(data.selected[0]);
                var textArea = "<textarea id='fileView' disabled=true rows='16' cols='80'></textarea>";
                var btn = "<button id='edit_btn' type='button' class='btn btn-info'>编辑etc</button>" +
                    "<button id='save_btn' type='button' style='margin-left: 30px' class='btn btn-info'>保存etc</button>";
                $("#textputer").empty().append(textArea);
                $("#btnputer").empty().append(btn);
                var path = selectedObj.id;
                $.ajax({
                    type: "post",
                    url: "/filePath",
                    data: {
                        "path": path
                    },
                    datetype: "json",
                    success: function (filedata) {
                        var context = filedata.responseContext[0];
                        for (var i = 1; i < filedata.responseContext.length; i++)
                            context += "\r\n" + filedata.responseContext[i];
                        $("#fileView").val(context);
                    },
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                        alert("很抱歉，文件打开失败，原因：" + errorThrown);
                    }
                })
                //文本设置可编辑
                $("#edit_btn").click(function(){
                    $("#fileView").attr("disabled",false);
                });
                //保存文本更改
                $("#save_btn").click(function(){
                    $("#fileView").attr("disabled",true);
                    var filecontent=$("#fileView").val();
                    $.ajax({
                        type:"post",
                        url:"/fileSave",
                        data:{
                            "path":path,
                            "filecontent":filecontent
                        },
                        datetype:"json",
                        success:function(data){
                            alert("保存成功");
                            $("#fileView").val(data.responseContext);
                        },
                        error : function(XMLHttpRequest, textStatus, errorThrown) {
                            alert("很抱歉，文件保存失败，原因：" + errorThrown);
                        }
                    })
                });
            }
        })
        .jstree({
            'core' : {
                'data' : {
                    "url" : "/etcList.htm",
                    "data" : function (node) {
                        return { "id" : node.id };
                    }
                }
            }
        });
})