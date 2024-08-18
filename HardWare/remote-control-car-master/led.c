#include "led.h"
#include	"config.h"

void led_init(void)
{	
	P5M0 = 0x00; 		//设置准双向口
	P5M1 = 0x00;
}



















