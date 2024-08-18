#ifndef __SCHE_TIMER_H__
#define __SCHE_TIMER_H__
#include	"config.h"

typedef void (*func_trigger_ptr_t)(void);

// 添加非阻塞操作，定时器中断中准时调用
extern func_trigger_ptr_t func_10ms_trigger_ptr;
extern func_trigger_ptr_t func_500ms_trigger_ptr;

/*
** 函数: void sche_timer_init(u8 period_ms)
** 描述: 调度定时器初始化，硬编码为timer0，因此timer0不可以再选用为其他功能
** 参数: ms, 定时器周期，范围：1~71ms。自动适应主时钟.
** 返回: none.
*/
void sche_timer_init(u8 period_ms);

/*
** 函数: void sche_timer_start(void)
** 描述: 启动sche_timer
** 参数: none.
** 返回: none.
*/
void sche_timer_start(void);

/*
** 函数: void sche_timer_stop(void)
** 描述: 停止sche_timer
** 参数: none.
** 返回: none.
*/
void sche_timer_stop(void);

#endif