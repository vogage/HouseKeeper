#include <string.h>
#include "uart1.h"

static bit uart1_tx_busy;  // 发送忙flag

// 串口接收处理函数，必须是非阻塞操作
func_uart_recv_parse_t func_usart1_recved_parse_ptr = NULL_PTR;

void uart1_init(u32 buard, func_uart_recv_parse_t func_parse_ptr)
{
	u16 buard_timer_reload_ticks = 0;
	uart1_tx_busy = 0;
	
	func_usart1_recved_parse_ptr = func_parse_ptr;
	
	EA = 0;
	// 除 P3.0 和 P3.1 外，其余所有 I/O 口上电后的状态均为高阻输入状态，用户在使用 I/O 口时必须先设置 I/O口模式
	P_SW1 &= 0x3F; 			// RXD/P3.0, TXD/P3.1
	SCON = 0x50;        // UART方式1；可变波特率8位数据方式; 允许串口接收数据
	
	buard_timer_reload_ticks = (65536 - SYSTEM_CLOCK / buard / 4);
	T2L = buard_timer_reload_ticks;
	T2H = buard_timer_reload_ticks >> 8;
	
	AUXR |= 0x15;    // 定时器2开始计数，不分频，作为波特率发生器
	ES   = 1;     //打开串行口中断
	EA   = 1;     //打开全局中断控制
}

void uart1_printf(u8 *p)				//发送字符串
{	
 	while (*p)
	{
		uart1_print_ascii(*p++);
	}  
}

// 不可重入，且存在阻塞操作，不能在中断服务函数中调用
u8 uart1_print_ascii(u8 dat)
{ 
	u8 ret = 0;
  while (uart1_tx_busy) {
		ret = 1;
	}
	uart1_tx_busy = 1;
	SBUF = dat;
	return ret;
}


// 重写putchar函数
char putchar(char c)
{
    uart1_print_ascii(c);
    return c;
}

void uart1_isr() interrupt 4
{
	u8 uart1_recv_char = 0;
	if (TI)
	{
		TI = 0;
		uart1_tx_busy = 0;
	}
	if (RI)
	{
		RI = 0;
		uart1_recv_char = SBUF;
		if (func_usart1_recved_parse_ptr != NULL_PTR) {
			func_usart1_recved_parse_ptr(uart1_recv_char);  // 不可阻塞
		}
	}
}























