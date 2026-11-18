import http from '../common/http.js'

export const registerApi = (param) => {
	return http.post('/user/user/register', param)
}

export const loginApi = (param) => {
	return http.post('/user/user/login', param)
}

export const updatePasswordApi = (param) => {
	return http.post('/user/user/updatePassword', param)
}

export const getUserInfoApi = (param) => {
	return http.get('/user/user/getUserInfo', param)
}

export const editInfoApi = (param) => {
	return http.post('/user/user/editInfo', param)
}
