#include <string.h>
#include "cmd_parse.h"

//0xaa 0x00 0x01 0x04 0x03 0x05 0x06 0x06
// 命令帧格式： 
// 8字节，首字节0xAA, 末字节为：sum(byte[0]~byte[6])
#define CMD_FRAME_START        0xAA  
unsigned char cmd_buff[CMD_FRAME_LEN] = {0};    // 用于存储最新解析得到的完整cmd
unsigned char cmd_recvd_flag = 0;

// 串口接收缓存
unsigned char uart_recv_buff[CMD_FRAME_LEN] = {0};  // 用于临时存储接收中断的字节
unsigned char uart_recved_buff_len = 0;

u8 uart_get_lasted_cmd(u8 * cmd_ptr, u8 cmd_len){
  if((cmd_len != CMD_FRAME_LEN) || (cmd_recvd_flag == 0)){
    return 1;
  }
  memcpy(cmd_ptr, cmd_buff, CMD_FRAME_LEN);
  // critical_enter();//关闭全局中断
	cmd_recvd_flag = 0;
	// critical_exit();//打开全局中断
  return 0;
}

void uart_recv_parse_by_char(u8 recv_char){
	u8 index = 0;
	u8 check_sum = 0;
	if (uart_recved_buff_len == 0) {
		if (recv_char == 0xAA) {
			uart_recv_buff[uart_recved_buff_len++] = recv_char;
		}
	}
	else {
		uart_recv_buff[uart_recved_buff_len++] = recv_char;
	}
	if (uart_recved_buff_len >= CMD_FRAME_LEN) {
		// 判断是否满足协议
		for(index = 0; index<CMD_FRAME_LEN-1; index++) {
			check_sum += uart_recv_buff[index];
		}
		if (uart_recv_buff[CMD_FRAME_LEN-1] == check_sum) {
			memcpy(cmd_buff, uart_recv_buff, CMD_FRAME_LEN);
			uart_recved_buff_len = 0;
			// critical_enter();//关闭全局中断
			cmd_recvd_flag = 1;
			// critical_exit();//打开全局中断
		}
		else {
			// 检索字符串中的第二个0xAA， 并整体平移到字符串首
			for (index = 1; index<CMD_FRAME_LEN; index++) {
				if (uart_recv_buff[index] == 0xAA) {
					memcpy(uart_recv_buff, uart_recv_buff+index, CMD_FRAME_LEN-index);
					uart_recved_buff_len = CMD_FRAME_LEN-index;
					break;
				}
			}
			if (uart_recved_buff_len >= CMD_FRAME_LEN) {
				uart_recved_buff_len = 0;
			}
		}
	}
}

