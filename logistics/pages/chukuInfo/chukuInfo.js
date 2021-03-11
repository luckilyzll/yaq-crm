// pages/chukuInfo/chukuInfo.js
Page({

    /**
     * 页面的初始数据
     */
    data: {
        navid1: 2,
        index: 0,
        czindex: 0,
        ycarray: ['异常情况1', '异常情况2', '异常情况3', '异常情况4'],
        czarray: ['处置情况1', '处置情况2', '处置情况3', '处置情况4'],
        zt: [
            {
                id: 1,
                url1: "../image/xuankuang.png",
                url2: "../image/xuankuang2.png",
                name: "正常"
            },
            {
                id: 2,
                url1: "../image/xuankuang.png",
                url2: "../image/xuankuang2.png",
                name: "异常"
            }
        ]

    },

    completeTap: function () {
        wx.navigateTo({
            url: '../complete/complete'
        })
    },

    bindPickerChange: function (e) {
        this.setData({
            index: e.detail.value,
        })
    },
    bindChangecz: function (e) {
        this.setData({
            czindex: e.detail.value,
        })
    },

    bindtapzc: function (e) {
        var navid = e.currentTarget.dataset.id
        // console.log(navid)
        this.setData({
            navid1: navid
        })
    },
    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {

    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady: function () {

    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow: function () {

    },

    /**
     * 生命周期函数--监听页面隐藏
     */
    onHide: function () {

    },

    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload: function () {

    },

    /**
     * 页面相关事件处理函数--监听用户下拉动作
     */
    onPullDownRefresh: function () {

    },

    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom: function () {

    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage: function () {

    }
})