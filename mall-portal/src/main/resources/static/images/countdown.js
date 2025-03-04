self.onmessage = function(e) {
    // let endTime = e.data; // 修正这里
    // let startTime = new Date().getTime(); // 初始化开始时间
    // console.log('endTime and startTime', endTime, startTime);
	self.postMessage({ type: 'log', message: 'endTime and startTime'});
    // let interval = setInterval(() => {
    //     let remainingTime = endTime - new Date().getTime(); // 使用当前时间计算剩余时间
    //     if (remainingTime <= 0) {
    //         clearInterval(interval);
    //         self.postMessage({ hours: 0, minutes: 0, seconds: 0 }); // 修正语法
    //     } else {
    //         let totalSeconds = Math.floor(remainingTime / 1000);
    //         let hours = Math.floor(totalSeconds / 3600);
    //         let minutes = Math.floor((totalSeconds % 3600) / 60);
    //         let seconds = totalSeconds % 60;
    //         self.postMessage({ hours, minutes, seconds });
    //     }
    // }, 1000);
};