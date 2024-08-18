#include "sche_timer.h"

// 添加非阻塞操作，定时器中断中准时调用
func_trigger_ptr_t func_10ms_trigger_ptr = NULL_PTR;
func_trigger_ptr_t func_500ms_trigger_ptr = NULL_PTR;

/*
** 函数: void sche_timer_init(u8 period_ms)
** 描述: 调度定时器初始化，硬编码为timer0，因此timer0不可以再选用为其他功能
** 参数: ms, 定时器周期，范围：1~71ms。自动适应主时钟.
** 返回: none.
*/
void sche_timer_init(u8 period_ms)
{
		u16 timer0_reload_tickes = 0;
	
		EA = 0;
	
		// 设置定时器模式为模式1（16位定时器）
    TMOD &= 0xF0;  //  清除定时器0模式位
    TMOD |= 0x00;   // 设置为模式0, 16位自动重载模式
		AUXR &= 0x7F;   // T0x12=0, 12分频
    // 计算定时器重载值，（系统时钟为11.0592MHz）
    timer0_reload_tickes = 65535 - period_ms * (SYSTEM_CLOCK / 12 / 1000);
    // 设置定时器初值
    TL0 = timer0_reload_tickes % 256;
    TH0 = timer0_reload_tickes / 256;
    // 启用定时器0中断
    ET0 = 1;
    // 关闭定时器0
    TR0 = 0;
	
		// 允许全局中断
    EA = 1;
}

void sche_timer_start(void)
{
	// 启动定时器0
	TR0 = 1;
}

void sche_timer_stop(void)
{
	// 停止定时器0
	TR0 = 0;
}

// sche_timer中断服务程序
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
		
		// 复位
		if (sche_timer_ticks >= 500){
			sche_timer_ticks = 0;
		}
}

