import { defineConfig, loadEnv } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'

const env = loadEnv(process.env.NODE_ENV, __dirname)

// 读取 manifest.json ，修改后重新写入
const fs = require('fs');

const manifestPath = './src/manifest.json';
let Manifest = fs.readFileSync(manifestPath, { encoding: 'utf-8' });

function replaceManifest(path, value) {
	const arr = path.split('.');
	const len = arr.length;
	const lastItem = arr[len - 1];

	let i = 0;
	let ManifestArr = Manifest.split(/\n/);

	for (let index = 0; index < ManifestArr.length; index++) {
		const item = ManifestArr[index];
		if (new RegExp(`"${arr[i]}"`).test(item)) ++i;
		if (i === len) {
			const hasComma = /,/.test(item);
			ManifestArr[index] = item.replace(
				new RegExp(`"${lastItem}"[\\s\\S]*:[\\s\\S]*`),
				`"${lastItem}" : ${value}${hasComma ? ',' : ''}`
			);
			break;
		}
	}

	Manifest = ManifestArr.join('\n');
}

let hasModify = false;
// 使用
if  (env.VITE_APP_BASE_PATH) {
  replaceManifest('h5.router.base', '"' + (env.VITE_APP_BASE_PATH || '/') + '"');
  hasModify = true;
}

if (hasModify) {
  fs.writeFileSync(manifestPath, Manifest, {
    flag: 'w',
  });
}

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    uni(),
  ],
})