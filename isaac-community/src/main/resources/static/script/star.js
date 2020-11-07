/**
 *  回答点赞处理JS
 */


/**
 * 显示回答点赞数
 *
 * @param satr
 * @returns {string}
 */
function showAnswerStar(satr) {
    if(undefined == satr ||  0 == satr){
        return "点赞";
    }

    return satr + " 条点赞";
}


/**
 * 给指定回答点赞或者取消点赞
 *
 * @param   qid    问题ID
 * @param   aid    回答ID
 */
function star1(qid, aid) {
    if(isNullOrEmpty(aid) || isNullOrEmpty(qid)){
        return;
    }

    $.ajax({
        url:  "/answer/starAction?aid=" + aid + "&qid=" + qid,
        type: "GET",
        contentType:"application/json",
        success:function (data) {
            if(data){
                $("#" + aid + "stars1").html("&nbsp;&nbsp;" + data.stars);
            }
        }
    });
}

function star2(qid, aid) {
    if(isNullOrEmpty(aid) || isNullOrEmpty(qid)){
        return;
    }

    $.ajax({
        url:  "/answer/starAction?aid=" + aid + "&qid=" + qid,
        type: "GET",
        contentType:"application/json",
        success:function (data) {
            if(data){
                $("#" + aid + "stars2").html("&nbsp;&nbsp;" + data.stars);
            }
        }
    });
}

function star3(qid, aid) {
    if(isNullOrEmpty(aid) || isNullOrEmpty(qid)){
        return;
    }

    $.ajax({
        url:  "/answer/starAction?aid=" + aid + "&qid=" + qid,
        type: "GET",
        contentType:"application/json",
        success:function (data) {
            if(data){
                $("#" + aid + "stars3").html("&nbsp;&nbsp;" + data.stars);
            }
        }
    });
}

