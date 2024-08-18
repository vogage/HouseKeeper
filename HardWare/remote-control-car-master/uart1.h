#ifndef __UART1_H__
#define __UART1_H__

#include "config.h"

// 由于串口输出函数不可重入且串口中断优先级较低，所有串口输出函数均不可在中断服务函数中调用，尤其是高优先级
// uart1重写了puts_char函数，可直接使用printf函数

typedef void (*func_uart_recv_parse_t)(u8 recv_char);

/*
** 函数: void uart1_init(u32 buard);
** 描述: uart1初始化，使用定时器2作为波特率发生器，因此timer2不应当作为其他用途
** 参数: buard, 波特率。自动适应主时钟.
**       func_parse_ptr, 按字符进行操作的接收处理函数，不可阻塞，可重入
** 返回: none.
*/
void uart1_init(u32 buard, func_uart_recv_parse_t func_parse_ptr);

/*
** 函数: void uart1_printf(unsigned char *p);
** 描述: 发送字符串
** 参数: p: 字符串指针
** 返回: none.
*/
void uart1_printf(u8 *p);

/*
** 函数: u8 uart1_print_ascii(unsigned char c);
** 描述: 发送字符
** 参数: c： 字符
** 返回: 0：发送成功， 1：发送失败
*/
u8 uart1_print_ascii(u8 dat);


#endif