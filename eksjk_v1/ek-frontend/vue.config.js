module.exports = {
  publicPath: '/',
  devServer: {
    // proxy: 'http://122.224.146.4',
    // proxy: 'http://106.75.80.67:8000',
    proxy: 'http://localhost:8000',
    public: '106.75.80.67:8081'
  },
  productionSourceMap: false
}