#ifndef __CMD_PARSE_H__
#define __CMD_PARSE_H__
#include "config.h"

#define CMD_FRAME_LEN       8
#define CMD_FRAME_HEAD      0
#define CMD_FRAME_FORWD     1
#define CMD_FRAME_BACK      2
#define CMD_FRAME_LEFT      3
#define CMD_FRAME_RIGHT     4
#define CMD_FRAME_BITS0     5
#define CMD_FRAME_BITS1     6
#define CMD_FRAME_CHECK     7

// 中断处理函数中调用，不可阻塞
void uart_recv_parse_by_char(u8 recv_char);

u8 uart_get_lasted_cmd(u8 * cmd_ptr, u8 cmd_len);

#endif
