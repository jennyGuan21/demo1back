from flask import Flask, jsonify

app = Flask(__name__)

@app.route('/hello')
def hello():
    return jsonify({"code":200,"msg":"接口运行成功"})

@app.route('/api/calculate', methods=['POST'])
def calculate():
    # 这里写你的风控计算逻辑
    return jsonify({
        "保护价": 8755,
        "服务费": 170,
        "风险改善率": "50.0%"
    })

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=10000)