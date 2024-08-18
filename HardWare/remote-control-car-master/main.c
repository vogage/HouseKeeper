#include "config.h"
#include "delay.h"
#include "uart1.h"
#include "cmd_parse.h"
#include "led.h"
#include "motor.h"
#include "sche_timer.h"

/*************	本地常量声明	**************/


/*************	本地变量声明	**************/


/*************	本地函数声明	**************/
// 后台程序，中断服务程序，非阻塞，准时调用,不可处理阻塞类操作（比如printf）
void func_10ms_trigger(void){
}

void func_500ms_trigger(void){
}

// 前台程序，主程序，轮询，执行时间不确定，可处理阻塞类操作
void main_task(void){
	u8 cmd_latest[8] = {0};

  while(1){
    if (0 == uart_get_lasted_cmd(cmd_latest, CMD_FRAME_LEN)){
			P55 = !P55;
    }
    motor_control(cmd_latest[CMD_FRAME_FORWD], cmd_latest[CMD_FRAME_BACK], \
									cmd_latest[CMD_FRAME_LEFT], cmd_latest[CMD_FRAME_RIGHT]);
	}
}

/*************  外部函数和变量声明 *****************/



/******************** 主函数 **************************/
void main(void){
	// 初始化
	led_init();
	uart1_init(9600, uart_recv_parse_by_char);
	motor_init();
	sche_timer_init(1);

	// 加载执行函数
	func_10ms_trigger_ptr = func_10ms_trigger;
	func_500ms_trigger_ptr = func_500ms_trigger;

	// 启动前后台任务处理
	sche_timer_start();
	main_task();
	
	while(1);
}
