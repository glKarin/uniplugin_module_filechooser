
					Test_native: function(multiple) {
						const uniplugin_module_filechooser = uni.requireNativePlugin('uniplugin_module_filechooser');
						uniplugin_module_filechooser.OpenFileChooser({
							mime: '*/*',
							multiple: multiple,
						}, 
						res => { // 成功回调
							console.log(res);
							uniplugin_module_filechooser.Log_e(JSON.stringify(res));
							/* uni.showModal({
								title: '结果',
								content: JSON.stringify(res),
							}); */
							if (!res.data) { // 这样判断是否选择了文件
								uni.showToast({
									duration: 2000,
									title: '请选择文件',
									icon: 'none'
								})
								return;
							}
							console.log("FileUploadFileUploadFileUpload");
							uniplugin_module_filechooser.Log_e("FileUploadFileUpload");
							uniplugin_module_filechooser.FileUpload({
								url: 'https://pn2.pengniaoyun.com/api/v3/task/upload_task_intro_file', // url
								method: 'get', // 请求方法, 小写, 可为空, 如果为空, 如果有文件则为post, 否则为get
								//data: '{"task_id":195}',
								//data: 'task_id=195&page_no=1&page_size=20',
								data: {
									'remark': '我的备注',
								},
								// 数据, 和dataType规则对应.
								// dataType == json时, data可为js对象, 或js对象的json字符串(插件会尝试解析为json对象)
								// dataType == text时, data可为字符串, 或js对象(插件会转换为json字符串)
								dataType: 'json', // data的类型, 可为空(根据data类型判断: data为js对象则为json, 否则为text). json: data应为js对象, 但如果data为字符串则尝试转换为json对象. text: data应为字符串, 但如果data为js对象, 则转换为json字符串.
								responseType: 'json', // 响应数据类型, 即第一个回调的参数类型, json: 转换为js对象, text: 默认, 文本
								files: { // 文件, 如果值为字符串, 相当于 {'file': '文件路径'}
									'file': res.data[0].path, // '字段名': '文件路径', ......
								}, // 单个文件且字段名为file等同于: files: res.data[0].path,
								header: { // 请求头 可为空
									//'Content-Type': '' // 规则如下:
									// 1. files不为空时插件会自动强制设置为multipart/form-data. 此时如果dataType被插件解释为text, 则忽略data字段数据.
									// 2. files为空时, 如果dataType最终被插件解释为json, 如果app设置Content-Type为application/json, 则为data会被转换为json字符串, Content-Type也为application/json
									// 3. files为空时, 如果dataType最终被插件解释为json, 如果app没有设置Content-Type, 或者Content-Type设为application/x-www-form-urlencoded, 则为data会被转换为key1=value1&key2=value2&...字符串, Content-Type被设为application/x-www-form-urlencoded
									// 4. files为空时, 如果dataType最终被插件解释为text, 则由app自行设置Content-Type, 插件不会自动设置.
									'Authorization': 'Bearer 11111',
								},
								timeout: 6000, // 超时 毫秒, 可为空, 默认0, 不限制
								// sslVerify: false, // ssl认证, 暂时没生效, 不会去认证
							},
							// 成功, res依赖传入的responseType字段的类型, 缺省为字符串, 如果是json则为js对象
							(res) => {
									console.log(res);
									uniplugin_module_filechooser.Log_e(res);
									uni.showModal({
										title: '结果: ' + typeof(res),
										content: typeof(res) == 'object' ? JSON.stringify(res) : res,
									});
								},
							// 失败, res为字符串
							(res) => {
								uni.showModal({
									title: 'fu错误',
									content: res,
								});
									console.log("fu: " + res);
									uniplugin_module_filechooser.Log_e("fu: " + res);
								},
							// 最终, 无参数
							() => {
									console.log("上传最终执行");
									uniplugin_module_filechooser.Log_e("上传最终执行");
								});
						},
						// 失败回调, res为字符串
						(res) => {
								uni.showModal({
									title: 'fc错误',
									content: res,
								});
								console.log("fc: " + res);
								uniplugin_module_filechooser.Log_e("fc: " + res);
							},
					// 最终回调, 无参数
						() => {
								uniplugin_module_filechooser.Log_e("选择文件最终执行");
								console.log("选择文件最终执行");
							}
						);
					},