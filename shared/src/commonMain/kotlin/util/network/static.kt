package util.network

import config.BaseUrlConfig

fun getAvatarStatic(avatar:String):String{
    return  "${BaseUrlConfig.UserAvatar}/${avatar}"
}