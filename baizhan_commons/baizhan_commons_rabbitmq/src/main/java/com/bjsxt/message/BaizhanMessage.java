package com.bjsxt.message;

import lombok.Data;

import java.io.Serializable;

/**
 * 百战电商平台中的消息接口，标记接口，只要实现，就是当前系统中的消息类型
 */
@Data
public interface BaizhanMessage extends Serializable {

}
