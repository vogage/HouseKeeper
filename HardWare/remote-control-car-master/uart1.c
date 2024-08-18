#include <string.h>
#include "uart1.h"

static bit uart1_tx_busy;  // ����æflag

// ���ڽ��մ������������Ƿ���������
func_uart_recv_parse_t func_usart1_recved_parse_ptr = NULL_PTR;

void uart1_init(u32 buard, func_uart_recv_parse_t func_parse_ptr)
{
	u16 buard_timer_reload_ticks = 0;
	uart1_tx_busy = 0;
	
	func_usart1_recved_parse_ptr = func_parse_ptr;
	
	EA = 0;
	// �� P3.0 �� P3.1 �⣬�������� I/O ���ϵ���״̬��Ϊ��������״̬���û���ʹ�� I/O ��ʱ���������� I/O��ģʽ
	P_SW1 &= 0x3F; 			// RXD/P3.0, TXD/P3.1
	SCON = 0x50;        // UART��ʽ1���ɱ䲨����8λ���ݷ�ʽ; �����ڽ�������
	
	buard_timer_reload_ticks = (65536 - SYSTEM_CLOCK / buard / 4);
	T2L = buard_timer_reload_ticks;
	T2H = buard_timer_reload_ticks >> 8;
	
	AUXR |= 0x15;    // ��ʱ��2��ʼ����������Ƶ����Ϊ�����ʷ�����
	ES   = 1;     //�򿪴��п��ж�
	EA   = 1;     //��ȫ���жϿ���
}

void uart1_printf(u8 *p)				//�����ַ���
{	
 	while (*p)
	{
		uart1_print_ascii(*p++);
	}  
}

// �������룬�Ҵ��������������������жϷ������е���
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


// ��дputchar����
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
			func_usart1_recved_parse_ptr(uart1_recv_char);  // ��������
		}
	}
}























