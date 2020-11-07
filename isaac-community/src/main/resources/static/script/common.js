/**
 *  前端页面通用函数定义JS
 */


function closeOpenPage() {
    layer.closeAll('iframe');//关闭弹窗
}


/**
 * 判空
 *
 * @param str
 * @returns {boolean}
 */
function isNullOrEmpty(str){
    if(null == str || undefined == str || "" == str){
        return true;
    }

    return false;
}

/**
 * 验证用户名格式
 *
 * @param   username     要验证的用户名
 * @returns {boolean}   验证通过:true ; 不通过:false
 */
function validateUsername(username) {
    var myreg = /^(?!_)(?!.*?_$)[a-zA-Z0-9_\u4e00-\u9fa5]{2,10}$/;
    if (!myreg.test(username)) {
        return false;
    }

    return true;
}

/**
 * 判断是否为手机号
 *
 * @param   pone
 * @returns {boolean}
 */
function isPhoneValidate(pone) {
    var myreg = /^[1][3,4,5,7,8][0-9]{9}$/;
    if (!myreg.test(pone)) {
        return false;
    }

    return true;
}

/**
 * 判断是否为电话号码
 *
 * @param   tel
 * @returns {boolean}
 */
function isTelValidate(tel) {
    var myreg = /^(([0\+]\d{2,3}-)?(0\d{2,3})-)(\d{7,8})(-(\d{3,}))?$/;
    if (!myreg.test(tel)) {
        return false;
    }

    return true;
}

/**
 * 验证密码格式
 *
 * @param   password     要验证的密码
 * @returns {boolean}   验证通过:true ; 不通过:false
 */
function validatePassword(password) {
    var myreg = /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}$/;
    if (!myreg.test(password)) {
        return false;
    }

    return true;
}

/**
 * 验证验证码格式
 *
 * @param   vercode     要验证的验证码
 * @returns {boolean}   验证通过:true ; 不通过:false
 */
function isVercodeValidate(vercode) {
    var myreg = /^[0-9]{6}$/;
    if (!myreg.test(vercode)) {
        return false;
    }

    return true;
}



/**
 * 去除文本中HTML标记
 *
 * @param str
 * @returns {*}
 */
function removeHTMLTag(str) {
    str = str.replace(/<\/?[^>]*>/g,''); //去除HTML tag
    str = str.replace(/[ | ]*\n/g,'\n'); //去除行尾空白
    //str = str.replace(/\n[\s| | ]*\r/g,'\n'); //去除多余空行
    str=str.replace(/&nbsp;/ig,'');//去掉&nbsp;
    str=str.replace(/\s/g,''); //将空格去掉
    return str;
}

/**
 * 时间戳转成时间
 *
 * @param timestamp
 * @returns {*}
 */
function timestampToTime(timestamp) {
    var date = new Date(timestamp);
    Y = date.getFullYear() + '-';
    M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-';
    D = date.getDate() + ' ';
    h = date.getHours() + ':';
    m = date.getMinutes() + ':';
    s = date.getSeconds();

    return Y+M+D+h+m+s;
}

/**
 * 当前页面加载为首页
 */
function loadIndex() {
    window.location.href = "/question/index";
}


/**
 * 内容检索
 */
function search() {
    var words = $("#words").val();

    if(isNullOrEmpty(words)){
        return;
    }

    // $.ajax({
    //     url:  "/search/text?words=" + words,
    //     type: "GET",
    //     contentType:"application/json",
    //     success:function (data) {
    //     }
    // });

    layui.use('layer', function() {
        var layer = layui.layer;
        layer.open({
            type: 2,
            title: false,
            content: "/search/text?words=" + words,
            area: ['600px', '600px'],
            end: function (data) {
                refresh();
            }
        });
    });

}



/**
 *  提问问题
 */
function addQuestion(){
    layui.use('layer', function() {
        var layer = layui.layer;
        layer.open({
            type: 2,
            title: false,
            content: ["/question/question", 'no'],
            area: ['600px', '600px'],
            end: function (data) {
                refresh();
            }
        });
    });
}

/**
 * 打开问题详情页面
 *
 * @param  qid
 */
function showQuestionDetail(qid) {
    if(!isNullOrEmpty(qid)){
        window.open("/question/toQuestionDetail?qid=" + qid);
    }
}


/**
 * 打开编辑回答页面
 *
 * @param  qid
 * @param  aid
 */
function editAnswer(qid,aid) {
    if(!isNullOrEmpty(qid) && !isNullOrEmpty(aid)){
        window.open("/answer/toEditAnswer?qid=" + qid + "&aid=" + aid);
    }
}

/**
 * 显示用户信息页面
 */
function showUserPage() {
    window.location.href = "/user/toUserPage";
}


/**
 * 登录页面
 */
function showLoginPage() {
    $("#form").attr("action","/login/getLoginPage");
    $("#form").submit();
}

/**
 * 注册页面
 */
function showRegisterPage() {
    $("#form").attr("action", "/register/getPage");
    $("#form").submit();
}

/**
 * 注销
 */
function logout() {
    $("#form").attr("action","/login/logout");
    $("#form").submit();
}

/**
 * 收藏回答
 */
function collectionAnswer(aid){
    if(!isNullOrEmpty(aid)){
        $.ajax({
            url:  "/answer/collectAnswer",
            type: "POST",
            data:JSON.stringify({"id": aid}),
            contentType:"application/json",
            dataType: "json",
            success:function (data) {
                if(data){
                    layui.use('layer', function(){
                        var layer = layui.layer;
                        if(1 == data.code){
                            layer.msg("收藏成功！");
                        }
                        if(2 == data.code){
                            layer.msg("已收藏！");
                        }

                    });
                }
            }
        });
    }
}

/**
 * 取消收藏
 */
function cancelCollectionAnswer(aid){
    if(!isNullOrEmpty(aid)){
        $.ajax({
            url:  "/answer/cancelCollect",
            type: "POST",
            data:JSON.stringify({"id": aid}),
            contentType:"application/json",
            dataType: "json",
            success:function (data) {
                if(data){
                    layui.use('layer', function(){
                        var layer = layui.layer;
                        if(1 == data.code){
                            layer.msg("取消收藏成功！");
                        }
                        if(2 == data.code){
                            layer.msg("已取消！");
                        }

                    });
                }
            }
        });
    }
}

/**
 * 关注问题
 */
function attentionQuestion(qid){
    if(!isNullOrEmpty(qid)){
        $.ajax({
            url:  "/question/attentionQuestion",
            type: "POST",
            data:JSON.stringify({"id": qid}),
            contentType:"application/json",
            dataType: "json",
            success:function (data) {
                if(data){
                    layui.use('layer', function(){
                        var layer = layui.layer;
                        if(1 == data.code){
                            layer.msg("关注成功！");
                        }
                        if(2 == data.code){
                            layer.msg("已关注！");
                        }

                    });
                }
            }
        });
    }
}

/**
 * 取消关注
 */
function cancelAttentionQuestion(qid){
    if(!isNullOrEmpty(qid)){
        $.ajax({
            url:  "/question/cancelAttention",
            type: "POST",
            data:JSON.stringify({"id": qid}),
            contentType:"application/json",
            dataType: "json",
            success:function (data) {
                if(data){
                    layui.use('layer', function(){
                        var layer = layui.layer;
                        if(1 == data.code){
                            layer.msg("取消关注成功！");
                        }
                        if(2 == data.code){
                            layer.msg("已取消！");
                        }

                    });
                }
            }
        });
    }
}

/**
 * 获取系统中最新提问的问题
 */
function getNewestQuestions(table) {
    table.render({
        elem: '#newestQuestions'
        ,url: "/question/getNewestQuestions"
        ,page: false
        ,cols: [
            [
                {field:'title', title: '最新问题',sort: false}
            ]
        ]
    });

    //监听行单击事件
    table.on('row(newQ)', function(obj){
        //console.log(obj.tr) //得到当前行元素对象
        //console.log(obj.data) //得到当前行数据
        showQuestionDetail(obj.data.id);
    });

}


/**
 * 管理员奖励积分给用户
 */
function rewardScore(userID) {
    $.ajax({
        url:  "/adminErrorMSG/rewardScore",
        type: "POST",
        data:{userID: userID},
        success:function (data) {
            if(data){
                layui.use('layer', function(){
                    var layer = layui.layer;
                    if(1 == data.code){
                        layer.msg("已奖励50积分！");
                    }
                    if(2 == data.code){
                        layer.msg("奖励积分失败！");
                    }

                });
            }
        }
    });
}