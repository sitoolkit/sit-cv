module.exports = {
  outputDir: "../sit-cv-core/src/main/resources/static",
  publicPath: "./",
  devServer: {
    port: 8081
  },
  configureWebpack: {
    devtool: "source-map"
  },
  productionSourceMap: false,
};
