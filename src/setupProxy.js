const proxy = require("http-proxy-middleware");

module.exports = function(app) {
  app.use(proxy("/proxy", {
    target: "http://localhost:5000/",
    changeOrigin: true,
    ws: true }));
  app.use(proxy("/search", {
    target: "https://kg.ebrains.eu",
    changeOrigin: true,
    ws: true }));
  app.use(proxy("/auth", {
    target: "https://kg.ebrains.eu",
    changeOrigin: true,
    ws: true }));
  app.use(proxy("/query", {
    target: "https://kg.ebrains.eu",
    changeOrigin: true,
    ws: true }));
};