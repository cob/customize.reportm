const {defineConfig} = require('@vue/cli-service')

const path = require("path");
const vueSrc = "./src";

const filename = "reportm-ui"

module.exports = defineConfig({
    transpileDependencies: true,
    css: {
        extract: {
            filename: filename + ".css",
        }
    },
    configureWebpack: {
        output: {
            filename: filename + ".js",
            libraryTarget: "umd",
            libraryExport: "default",
        },
        externals: {
            jquery: 'jQuery',
        },
        resolve: {
            alias: {
                "@": path.resolve(__dirname, vueSrc)
            },
            extensions: ['.ts', '.js', '.vue', '.json']
        },
    },
    chainWebpack: config => {
        config.optimization.delete('splitChunks')
        config.optimization.minimize(true)
    }
})

