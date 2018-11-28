app.service("uploadService",function($http){
	
	this.uploadFile = function(){
		// 向后台传递数据:创建from表单对象
		var formData = new FormData();
		// 向formData中添加数据:
		formData.append("file",file.files[0]);
		
		return $http({
			method:'post',
			url:'../upload/uploadFile.do',
			data:formData,
			// 'Content-Type':undefined：在浏览器进行提交的过程中会帮助
			// 程序加上enctype=multipart/form-data
			headers:{'Content-Type':undefined} ,
			// 将附件进行序列化
			transformRequest: angular.identity
		});
	}
	
});