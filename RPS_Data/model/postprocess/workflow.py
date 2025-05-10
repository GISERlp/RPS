import argparse
import numpy as np
import os

# 加载数据
gageinfoid = np.load(r"D:\github_desktop\RPS\RPS_Data\model\modeldata\gageinfoid.npy")  # 站点信息
obs = np.load(r"D:\github_desktop\RPS\RPS_Data\model\modeldata\obs44.npy")  # 径流观测数据
valida_pred = np.load(r'D:\github_desktop\RPS\RPS_Data\model\modeldata\valida_pred.npy')  # 径流模拟数据

# 定义函数来保存数据到文件
# def save_data_to_file(site_id, output_folder):
#     if site_id in gageinfoid:
#         index = np.where(gageinfoid == site_id)[0][0]
#         obs_data = obs[index]
#         valida_pred_data = valida_pred[index]
#
#         # 使用列表推导式来格式化数据，每个数据后都加上一个空格
#         valida_pred_data_str = ' '.join([f"{x:.7f} " for x in valida_pred_data])
#         obs_data_str = ' '.join([f"{x:.7f} " for x in obs_data])
#
#
#         obs_file = os.path.join(output_folder, "obs.txt")
#         valida_pred_file = os.path.join(output_folder, "pred.txt")
#
#         with open(obs_file, 'w') as f:
#             f.write(f"{site_id}_obs: {obs_data_str}\n")
#         print(f"观测数据已保存到: {obs_file}")
#
#         with open(valida_pred_file, 'w') as f:
#             f.write(f"{site_id}_pred: {valida_pred_data_str}\n")
#         print(f"模拟数据已保存到: {valida_pred_file}")
#     else:
#         print(f"没有找到站点 ID {site_id} 的数据。")

def save_data_to_file(site_id, output_folder):
    if site_id in gageinfoid:
        index = np.where(gageinfoid == site_id)[0][0]
        obs_data = obs[index]
        valida_pred_data = valida_pred[index]

        # 确保 valida_pred_data 是一维数组
        valida_pred_data = valida_pred_data.ravel()
        obs_data = obs_data.ravel()

        # 使用列表推导式来格式化数据，每个数据后都加上一个空格
        valida_pred_data_str = ' '.join([f"{x:.7f} " for x in valida_pred_data])
        obs_data_str = ' '.join([f"{x:.7f} " for x in obs_data])

        valida_pred_file = os.path.join(output_folder, "pred.txt")
        obs_file = os.path.join(output_folder, "obs.txt")

        with open(obs_file, 'w') as f:
            f.write(f"{site_id}_obs: {obs_data_str}\n")
        print(f"观测数据已保存到: {obs_file}")

        with open(valida_pred_file, 'w') as f:
            f.write(f"{site_id}_pred: {valida_pred_data_str}\n")
        print(f"模拟数据已保存到: {valida_pred_file}")
    else:
        print(f"没有找到站点 ID {site_id} 的数据。")


# 设置命令行参数
def main():
    parser = argparse.ArgumentParser(description="根据站点 ID 导出观测和模拟数据")
    parser.add_argument('site_id', type=int, help="站点编号")
    parser.add_argument('output_folder', type=str, help="输出文件夹路径")

    args = parser.parse_args()
    site_id = args.site_id
    output_folder = args.output_folder

    save_data_to_file(site_id, output_folder)

if __name__ == "__main__":
    main()
    # save_data_to_file(1013500, r"D:\github_desktop\RPS\RPS_Data\lp\径流预测\result")