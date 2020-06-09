package kr.smart.carefarm.data


data class LoginResponse(var code: String,
                         var message: String,
                         var returnUrl: String)