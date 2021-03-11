// pages/box/box.js
Page({

    /**
     * 页面的初始数据
     */
    data: {
        navid1: 1,
        nav: [
            {
                id: 1,
                name: '未完成',
            },
            {
                id: 2,
                name: '已完成',
            }
        ],
        wuliu: [
            {
                id: 1,
                dian: "配电器",
                name: "废旧箱式变电站",
                caozuo: '拆除入库',
                cheliang: "沪B343042",
                dizhi: "浦东",
                cangku: "奉贤神州路废旧厂库",
                shuliang: 1,
                shijian: "2020-03-30",
                tupian: "../image/rkchuzhi.png"
            },
            {
                id: 2,
                dian: "电缆",
                name: "废旧箱式变电站(AC10KV)",
                caozuo: '出库',
                cheliang: "沪B343042",
                dizhi: "浦东",
                cangku: "奉贤神州路废旧厂库",
                shuliang: 5,
                shijian: "2020-03-30",
                tupian: "../image/chuku.png"
            }
        ],
    },
    bindnav: function (e) {
        // debugger
        var navid = e.currentTarget.dataset.id
        // console.log(navid)
        this.setData({
            navid1: navid
        })
    },
    tijiaoTap: function () {
        wx.navigateTo({
            url: '../tijiaoInfo/tijiaoInfo'
        })
    },
    chukuTap: function () {
        wx.navigateTo({
            url: '../chukuInfo/chukuInfo'
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