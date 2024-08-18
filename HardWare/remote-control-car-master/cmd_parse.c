#include <string.h>
#include "cmd_parse.h"

//0xaa 0x00 0x01 0x04 0x03 0x05 0x06 0x06
// ����֡��ʽ�� 
// 8�ֽڣ����ֽ�0xAA, ĩ�ֽ�Ϊ��sum(byte[0]~byte[6])
#define CMD_FRAME_START        0xAA  
unsigned char cmd_buff[CMD_FRAME_LEN] = {0};    // ���ڴ洢���½����õ�������cmd
unsigned char cmd_recvd_flag = 0;

// ���ڽ��ջ���
unsigned char uart_recv_buff[CMD_FRAME_LEN] = {0};  // ������ʱ�洢�����жϵ��ֽ�
unsigned char uart_recved_buff_len = 0;

u8 uart_get_lasted_cmd(u8 * cmd_ptr, u8 cmd_len){
  if((cmd_len != CMD_FRAME_LEN) || (cmd_recvd_flag == 0)){
    return 1;
  }
  memcpy(cmd_ptr, cmd_buff, CMD_FRAME_LEN);
  // critical_enter();//�ر�ȫ���ж�
	cmd_recvd_flag = 0;
	// critical_exit();//��ȫ���ж�
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
		// �ж��Ƿ�����Э��
		for(index = 0; index<CMD_FRAME_LEN-1; index++) {
			check_sum += uart_recv_buff[index];
		}
		if (uart_recv_buff[CMD_FRAME_LEN-1] == check_sum) {
			memcpy(cmd_buff, uart_recv_buff, CMD_FRAME_LEN);
			uart_recved_buff_len = 0;
			// critical_enter();//�ر�ȫ���ж�
			cmd_recvd_flag = 1;
			// critical_exit();//��ȫ���ж�
		}
		else {
			// �����ַ����еĵڶ���0xAA�� ������ƽ�Ƶ��ַ�����
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

