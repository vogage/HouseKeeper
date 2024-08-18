#ifndef __SCHE_TIMER_H__
#define __SCHE_TIMER_H__
#include	"config.h"

typedef void (*func_trigger_ptr_t)(void);

// ��ӷ�������������ʱ���ж���׼ʱ����
extern func_trigger_ptr_t func_10ms_trigger_ptr;
extern func_trigger_ptr_t func_500ms_trigger_ptr;

/*
** ����: void sche_timer_init(u8 period_ms)
** ����: ���ȶ�ʱ����ʼ����Ӳ����Ϊtimer0�����timer0��������ѡ��Ϊ��������
** ����: ms, ��ʱ�����ڣ���Χ��1~71ms���Զ���Ӧ��ʱ��.
** ����: none.
*/
void sche_timer_init(u8 period_ms);

/*
** ����: void sche_timer_start(void)
** ����: ����sche_timer
** ����: none.
** ����: none.
*/
void sche_timer_start(void);

/*
** ����: void sche_timer_stop(void)
** ����: ֹͣsche_timer
** ����: none.
** ����: none.
*/
void sche_timer_stop(void);

#endif