import {
	createSSRApp
} from "vue";
import App from "./App.vue";
// 引入 uView UI
import uView from './uni_modules/vk-uview-ui';

export function createApp() {
	const app = createSSRApp(App);
	// 使用 uView UI
	app.use(uView)
	return {
		app,
	};
}