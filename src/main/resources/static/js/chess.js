var websocket = null;
var url = "ws://localhost:8080/chess";
var isWin = 0;//是否已经拼出胜负，0表示游戏还没有开始，1表示游戏还没有结束，-1表示游戏已经结束
var isChess = -1;//是否由该玩家下棋
var color = 0;//棋子的颜色

//在棋盘上绘制棋子
var addChess = function(chess_color, x, y){
    //下棋  画小圆圈	角度转弧度 π/180×角度	弧度变角度 180/π×弧度
    cxt.beginPath();//开始
    cxt.arc(x,y,12,0,Math.PI*2,true);//画棋子
    cxt.fillStyle = (chess_color == 1 ? "#FFFFFF" : "#000000");
    cxt.fill();
    cxt.closePath();//结束
}

$('#login').click(function(){
    //username，并且判断是否为null
    var username = $('#username').val();
    if(username == null || username== ""){
        alert("昵称不能为空");
        return;
    }

    //创建客户端，连接服务端
    websocket = new WebSocket(url + "/" +username);
    websocket.onopen = function(event){
        $('#show').append("<p>系统提示：连接成功</p>");
        //和服务端连接成功，把发送按钮设置为可点击状态，将开始游戏按钮设置为可点击，给登录按钮设置不可点击
        $('#sendMessage').removeAttr("disabled");
        $('#startGame').removeAttr('disabled');
        $('#login').attr("disabled","disabled");
    }

    websocket.onmessage = function(event){
        //let是es6新增语法，表示定义一个局部变量，var是全局变量
        let message = null;
        let rs = JSON.parse(event.data);
        //对服务端发送的消息进行判断
        if(rs.status == 1){//为1表示是系统消息
            if(rs.data=="START"){//提示两人游戏开始
                isGameStatus(1);//把游戏状态改为1，表示游戏进行中
                message = "<p><font color='red'>系统提示：游戏开始</font></p>";
            }else if(rs.data=="END"){//指令页面游戏结束
                isGameStatus(-1);
                let winUsername = rs.gamer.username;//赢家
                $('#show').append("<p><font color='red'>系统提示：恭喜，赢家是"+winUsername+"</font></p>");
                message = "<p><font color='red'>系统提示：游戏结束</font></p>";
            }else if(rs.data == "GAME_DATA"){
                color = rs.gamer.color;//为玩家分配棋子（颜色）
                if(rs.gamer.color == 1)
                    isChess = 1;
                message = "<p><font color='red'>系统提示：数据传输完成</font></p>";
            }else {
               message = "<p>系统提示：" + rs.data + "</p>";
            }
            $('#show').append(message);
        }else if(rs.status == 2){//为2表示是普通消息
            message = "<p>" + rs.data + "</p>";
            $('#show').append(message);
        }else if(rs.status == 3){//广播所有客户端绘制棋子
            if(rs.data == "PLAY_CHESS"){
                //执行下棋功能
                var chess_color = rs.gamer.color;//得到是哪个玩家下的棋子，chess_color表示棋子的颜色
                var x = rs.localtion.x;
                var y = rs.localtion.y;
                console.log("x="+x+",y="+y);
                //对i，j进行处理，x，y为在画布中的坐标
                var indexI = (y-15)/30;
                var indexJ = (x-15)/30;
                console.log("i="+indexI+",j="+indexJ);
                if(chess_color == 1){
                    chessData[indexI][indexJ] = 1;
                }else{
                    chessData[indexI][indexJ] = -1;
                }
                console.log("====>"+chess_color);

                //调用落子功能
                addChess(chess_color,x,y);

                isChess *=-1;//设置为对方可下棋
                if(chess_color == color){//只有下棋方才能判断胜负
                    console.log("判断胜负");
                    //判断胜负
                    judgeWin(indexI,indexJ);
                }
            }
        }

    }

    //出现错误时触发
    websocket.onerror = function(event){
        alert("连接出错");
    }
});

//发送消息
$('#sendMessage').click(function(){
    var message = $('#message').val();
    //把消息清空
    $('#message').val('');
    //把消息发送给服务端
    websocket.send(jsonMessage(2,message,null,null));
});

//点击游戏开始，进入准备状态
$('#startGame').click(function(){
    websocket.send(jsonMessage(1,"READ",null,null));
});

//发送给服务端的函数封装函数
var jsonMessage = function(status,data,x,y){
    result.status = status;
    result.data = data;
    result.gamer = null;
    result.localtion.x = x;
    result.localtion.y = y;
    var resultMessage = JSON.stringify(result);//把js对象转换成json字符串
    return resultMessage;
}

//消息体
var result = {
    status:null,
    data:null,
    gamer:null,
    localtion:{
        x:null,
        y:null
    }
}


<!-- 绘制棋盘 -->
var chess = $('#chess')[0];//得到棋盘元素
var cxt = chess.getContext('2d');//获得canvas进行绘图
cxt.strokeStyle = '#bfbfbf';//得到棋盘线的颜色颜色样式，即画笔颜色
var logo = new Image();//棋盘的背景样式
logo.src = 'image/timg.jpg';//棋盘背景色
logo.onload = function(){
    cxt.globalAlpha = 0.2;//设置整个canvas的透明度
    cxt.beginPath();//背景绘制
    cxt.drawImage(logo,0,0,450,450);
    cxt.closePath();
    drawChessBorad();//棋盘绘制
};

//绘制棋盘
var drawChessBorad = function(){
    cxt.globalAlpha = 1;
    cxt.save();
    for(i=0;i<15;i++){
        cxt.moveTo(15+i*30,15);
        cxt.lineTo(15+i*30,435);
        cxt.moveTo(15,15+i*30);
        cxt.lineTo(435,15+i*30);
    }
    cxt.stroke();//直接线条绘制，不用填充绘制
    cxt.restore();
}

//保存棋盘数据
//棋子数组，二维
//保存所下的棋子	0:未下，1：下白棋，-1：下黑棋
var chessData = new Array(15);
for(var i=0;i<15;i++){
    chessData[i] = new Array(15);
    for(var j=0;j<15;j++){
        chessData[i][j] = 0;
    }
}

<!-- 落子  -->
var i,j;//保存棋子在画布中的坐标，参照物为画布
//棋盘width=450，height=450，
//鼠标点在棋盘范围内 ，x坐标取值为left ~ left+450，y坐标取值为top ~ top+450
var left = chess.getBoundingClientRect().left;//画布最左上角坐标，参照物为整个屏幕,x
var top = chess.getBoundingClientRect().top;//y

//对画布做鼠标监听，判断游戏是否已经开始，以及判断要在哪个地方落子
var chessCanvas = function(event){
    //先判断游戏是否已经结束
    if(isWin == 0){
        alert("游戏还没有开始哦");
    }else if(isWin == -1){
        alert("游戏已经结束了哦");
    }else if(isWin == 1){
        if(isChess == 1){//表示轮到他下棋
            //event.x和event.y也是相对整个屏幕的坐标，所以需要减去棋盘左上角坐标，才是在棋盘上的坐标
            i = event.y - chess.getBoundingClientRect().top;//在画布内的y坐标
            j = event.x - chess.getBoundingClientRect().left;//在画布内的x坐标
            //符合这四个条件，说明鼠标点击在棋盘上了
            if(i>0 && i<450 && j>0 && j<450){
                //对x，y坐标做简单的处理,主要是为了获取坐标在画布内的位置对应在棋盘二维数组中的位置，比如在棋盘中第一行第一列的交叉点就是棋盘二维数组的arr[0][0]
                i = Math.round((i-15)/30);
                j = Math.round((j-15)/30);
                //鼠标点击在棋盘上，并且这个位置可以落子，那么就绘制棋子
                if(chessData[i][j] == 0){
                    //给服务端发消息，广播所有客户端绘制棋子，同步棋盘
                    websocket.send(jsonMessage(3,"CHESS",j*30+15,i*30+15));
                }else{
                    alert("此位置已经有棋子");
                }
            }
        }else{
            alert("还不到你下棋哦");
        }
    }
}



//判断胜负,参数chess为判断白棋还是黑棋
var judgeWin = function(i,j){
    var chess = chessData[i][j];//得到这个坐标的棋子,1为白棋，-1为黑棋
    var count = [0,0,0,0,0,0,0,0];//8个方向的连子数
    var flag = [true,true,true,true,true,true,true,true];//判断8个方向是否可以继续查找连子
    //判断所有赢法
    for(var k=1;k<=4;k++){//一共要判断4次
        //判断这个棋子是否在棋盘上
        if((i-k)>=0 && j>=0 && flag[0] == true){								//向上
            chessData[i-k][j] == chess ? count[0]++ : flag[0]=false;			//判断是否为连子
        }
        if((i-k)>=0 && (j+k)<15 && flag[1] == true) {
            chessData[i-k][j+k] == chess ? count[1]++ : flag[1]=false;			//向着右上
        }
        if(i>=0 && (j+k)<15 && flag[2] == true){
            chessData[i][j+k] == chess ? count[2]++ : flag[2]=false;			//向右
        }
        if((i+k)<15 && (j+k)<15 && flag[3] == true) {
            chessData[i+k][j+k] == chess ? count[3]++ : flag[3]=false;			//向右下
        }
        if((i+k)<15 && j>=0 && flag[4] == true) {
            chessData[i+k][j] == chess ? count[4]++ : flag[4]=false;			//向下
        }
        if((i+k)<15 && (j-k)>=0 && flag[5] == true) {
            chessData[i+k][j-k] == chess ? count[5]++ : flag[5]=false;			//向左下
        }
        if(i>=0 && (j-k)<15 && flag[6] == true) {
            chessData[i][j-k] == chess ? count[6]++ : flag[6]=false;			//向左
        }
        if((i-k)>=0 && (j-k)>=0 && flag[7] == true) {
            chessData[i-k][j-k] == chess ? count[7]++ : flag[7]=false;			//向左上
        }
    }
    console.log("=======>");
    if(count[0]+count[4]>=4 || count[1]+count[5]>=4 || count[2]+count[6]>=4 || count[3]+count[7]>=4){
        websocket.send(jsonMessage(1,"END",chess,chess));//给服务端发送系统消息，广播所有客户端赢家是谁
    }
    console.log(chess+" - "+count);

}

//改变游戏状态
var isGameStatus = function(num){
    isWin = num;
}