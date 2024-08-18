#include "sche_timer.h"

// ��ӷ�������������ʱ���ж���׼ʱ����
func_trigger_ptr_t func_10ms_trigger_ptr = NULL_PTR;
func_trigger_ptr_t func_500ms_trigger_ptr = NULL_PTR;

/*
** ����: void sche_timer_init(u8 period_ms)
** ����: ���ȶ�ʱ����ʼ����Ӳ����Ϊtimer0�����timer0��������ѡ��Ϊ��������
** ����: ms, ��ʱ�����ڣ���Χ��1~71ms���Զ���Ӧ��ʱ��.
** ����: none.
*/
void sche_timer_init(u8 period_ms)
{
		u16 timer0_reload_tickes = 0;
	
		EA = 0;
	
		// ���ö�ʱ��ģʽΪģʽ1��16λ��ʱ����
    TMOD &= 0xF0;  //  �����ʱ��0ģʽλ
    TMOD |= 0x00;   // ����Ϊģʽ0, 16λ�Զ�����ģʽ
		AUXR &= 0x7F;   // T0x12=0, 12��Ƶ
    // ���㶨ʱ������ֵ����ϵͳʱ��Ϊ11.0592MHz��
    timer0_reload_tickes = 65535 - period_ms * (SYSTEM_CLOCK / 12 / 1000);
    // ���ö�ʱ����ֵ
    TL0 = timer0_reload_tickes % 256;
    TH0 = timer0_reload_tickes / 256;
    // ���ö�ʱ��0�ж�
    ET0 = 1;
    // �رն�ʱ��0
    TR0 = 0;
	
		// ����ȫ���ж�
    EA = 1;
}

void sche_timer_start(void)
{
	// ������ʱ��0
	TR0 = 1;
}

void sche_timer_stop(void)
{
	// ֹͣ��ʱ��0
	TR0 = 0;
}

// sche_timer�жϷ������
void sche_timer_isr(void) interrupt 1{
    static u16 sche_timer_ticks = 0;
		sche_timer_ticks ++;

		if(sche_timer_ticks > 0 && sche_timer_ticks%10 == 0){  // 10ms
			if (NULL_PTR != func_10ms_trigger_ptr){
				func_10ms_trigger_ptr();
			}
		}
		
		if(sche_timer_ticks > 0 && sche_timer_ticks%500 == 0){  // 500ms
			if (NULL_PTR != func_500ms_trigger_ptr){
				func_500ms_trigger_ptr();
			}
		}
		
		// ��λ
		if (sche_timer_ticks >= 500){
			sche_timer_ticks = 0;
		}
}

