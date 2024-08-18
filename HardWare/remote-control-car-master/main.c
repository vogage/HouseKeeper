#include "config.h"
#include "delay.h"
#include "uart1.h"
#include "cmd_parse.h"
#include "led.h"
#include "motor.h"
#include "sche_timer.h"

/*************	���س�������	**************/


/*************	���ر�������	**************/


/*************	���غ�������	**************/
// ��̨�����жϷ�����򣬷�������׼ʱ����,���ɴ������������������printf��
void func_10ms_trigger(void){
}

void func_500ms_trigger(void){
}

// ǰ̨������������ѯ��ִ��ʱ�䲻ȷ�����ɴ������������
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

/*************  �ⲿ�����ͱ������� *****************/



/******************** ������ **************************/
void main(void){
	// ��ʼ��
	led_init();
	uart1_init(9600, uart_recv_parse_by_char);
	motor_init();
	sche_timer_init(1);

	// ����ִ�к���
	func_10ms_trigger_ptr = func_10ms_trigger;
	func_500ms_trigger_ptr = func_500ms_trigger;

	// ����ǰ��̨������
	sche_timer_start();
	main_task();
	
	while(1);
}
