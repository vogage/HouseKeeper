from flask import Flask, render_template, Response, make_response
from camera import VideoCamera


app = Flask(__name__)

#相机推流
def gen(camera):
    while True:
        frame = camera.get_frame()
        yield (b'--frame\r\n'
               b'Content-Type: image/jpeg\r\n\r\n' + frame + b'\r\n\r\n')
#相机喂流
@app.route('/video_feed')
def video_feed():
    cam = VideoCamera()
    frame_str_gen = gen(cam)
    resp = Response(frame_str_gen, mimetype='multipart/x-mixed-replace; boundary=frame')
    return resp

#当前实时相机画面
@app.route('/')
def cur_camera():
    return render_template('cur_camer.html')

if __name__ == '__main__':
    app.run()
