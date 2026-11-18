const baseUrl = 'http://localhost:10086';

export default {
    post(url, data) {
        return new Promise((resolve, reject) => {
            uni.request({
                url: baseUrl + url,
                method: 'POST',
                data: data,
                header: {
                    'Content-Type': 'application/json'
                },
                success: (res) => {
                    console.log('HTTP POST success:', res.data);
                    if (res.statusCode === 200) {
                        resolve(res.data);
                    } else {
                        uni.showToast({
                            title: res.data.msg || '请求失败',
                            icon: 'none'
                        });
                        reject(res.data);
                    }
                },
                fail: (err) => {
                    console.error('HTTP POST error:', err);
                    uni.showToast({
                        title: '网络请求失败',
                        icon: 'none'
                    });
                    reject(err);
                }
            });
        });
    },

    get(url, data) {
        return new Promise((resolve, reject) => {
            uni.request({
                url: baseUrl + url,
                method: 'GET',
                data: data,
                header: {
                    'Content-Type': 'application/json'
                },
                success: (res) => {
                    console.log('HTTP GET success:', res.data);
                    if (res.statusCode === 200) {
                        resolve(res.data);
                    } else {
                        uni.showToast({
                            title: res.data.msg || '请求失败',
                            icon: 'none'
                        });
                        reject(res.data);
                    }
                },
                fail: (err) => {
                    console.error('HTTP GET error:', err);
                    uni.showToast({
                        title: '网络请求失败',
                        icon: 'none'
                    });
                    reject(err);
                }
            });
        });
    }
};
