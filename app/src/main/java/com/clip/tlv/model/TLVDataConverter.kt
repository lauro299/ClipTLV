package com.clip.tlv.model

class TLVDataConverter{

    fun decode(data:String):List<Tag>{
        var list = ArrayList<Tag>()
        var index = 0
        var bufferData = data
        try{
            while(bufferData.isNotBlank() && bufferData.length > 2) {
                var length4Byte = false
                var command = readCommand(bufferData, index, 2)
                bufferData = bufferData.removeRange(index, command.command.length)
                var lenght = readLength(bufferData)!!
                if(command.command == "C282"){
                    lenght = readLength(bufferData, 4)!!
                    length4Byte = true
                }
                bufferData = bufferData.removeRange(0,if(length4Byte)4 else 2)
                var lenghtString= 0
                var value:String?=null
                if(lenght!=0){
                    value = when(command.type ){
                        TYPE_DATA.PRIMITIVE_DATA->{
                            lenghtString = lenght*2
                            decodePrimitiveData(bufferData,lenghtString)
                        }
                        TYPE_DATA.STRING_DATA->{
                            lenghtString = lenght*2
                            decodeString(bufferData,lenghtString)
                        }
                        TYPE_DATA.INTEGER_DATA->{
                            lenghtString = lenght*2
                            decodeString(bufferData,lenghtString)
                        }
                    }
                    bufferData = bufferData.removeRange(0, lenghtString)
                }
                list.add(Tag(command, lenght, value))
            }
        } catch (ex:Exception){
            ex.printStackTrace()
        }
        return list
    }

    fun readCommand(data:String,index:Int,size:Int):Command{
        val command = data.substring(index,size)
        if(Commands.containsKey(command)){
            return Commands[command]!!
        } else {
        }
        if(size == 4){
            if(command.startsWith("9F")){
                return Command(command, "Unknown tag")
            } else {
                if(command=="C282"){
                    return Command(command, "Unkown tag")
                } else {
                    command.substring(0,2)
                            .toIntOrNull(16)
                            ?.let {
                                return Command(it.toString(16), "Unknown tag")
                            }
                }
            }
            throw Exception("No command Found")
        }
        return readCommand(data,index,size*2)
    }

    fun readLength(data:String,size:Int = 2):Int?{
        var lenght = data.substring(0,size)
        return lenght.toIntOrNull(16)
    }

    fun decodePrimitiveData(data:String, size:Int):String{
        return data.substring(0,size)
    }

    fun decodeString(data:String,size:Int):String{
        var subData = data.substring(0, size)
        var array = subData.toCharArray()
        var buffer = StringBuffer()
        for(i in 0 until array.size step 2){
            buffer.append("${array[i]}${array[i+1]}".toInt(16).toChar())
        }
        return buffer.toString()
    }

}

data class Tag(var comand:Command, var lenght:Int,val value:String?)