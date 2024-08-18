#ifndef __UART1_H__
#define __UART1_H__

#include "config.h"

// ���ڴ�������������������Ҵ����ж����ȼ��ϵͣ����д�������������������жϷ������е��ã������Ǹ����ȼ�
// uart1��д��puts_char��������ֱ��ʹ��printf����

typedef void (*func_uart_recv_parse_t)(u8 recv_char);

/*
** ����: void uart1_init(u32 buard);
** ����: uart1��ʼ����ʹ�ö�ʱ��2��Ϊ�����ʷ����������timer2��Ӧ����Ϊ������;
** ����: buard, �����ʡ��Զ���Ӧ��ʱ��.
**       func_parse_ptr, ���ַ����в����Ľ��մ�����������������������
** ����: none.
*/
void uart1_init(u32 buard, func_uart_recv_parse_t func_parse_ptr);

/*
** ����: void uart1_printf(unsigned char *p);
** ����: �����ַ���
** ����: p: �ַ���ָ��
** ����: none.
*/
void uart1_printf(u8 *p);

/*
** ����: u8 uart1_print_ascii(unsigned char c);
** ����: �����ַ�
** ����: c�� �ַ�
** ����: 0�����ͳɹ��� 1������ʧ��
*/
u8 uart1_print_ascii(u8 dat);


#endif