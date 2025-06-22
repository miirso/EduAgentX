#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""
生成课程信息的Excel文件
"""

import pandas as pd
import random
import string
import datetime
from pathlib import Path

# 课程信息配置
courses_config = [
    {
        "name": "分布式系统",
        "description": "mit6.824明星课程同款",
        "subject_id": "46", # 计算机专业
        "assessment_method": "考试+考察",
        "type": "A" # 必修课
    },
    {
        "name": "操作系统",
        "description": "掌握操作系统核心原理",
        "subject_id": "46",
        "assessment_method": "考试",
        "type": "A"
    },
    {
        "name": "计算机网络",
        "description": "计算机网络基础与应用",
        "subject_id": "46",
        "assessment_method": "考试",
        "type": "A"
    },
    {
        "name": "数据库系统",
        "description": "关系型数据库设计与实现",
        "subject_id": "46",
        "assessment_method": "考试+实验",
        "type": "A"
    },
    {
        "name": "Web开发技术",
        "description": "现代Web应用开发实践",
        "subject_id": "46",
        "assessment_method": "考察",
        "type": "B" # 选修课
    },
    {
        "name": "人工智能导论",
        "description": "AI基础理论与应用",
        "subject_id": "46",
        "assessment_method": "考察",
        "type": "B"
    },
    {
        "name": "软件工程",
        "description": "软件开发流程与管理",
        "subject_id": "46",
        "assessment_method": "考试+项目",
        "type": "A"
    },
    {
        "name": "计算机图形学",
        "description": "图形渲染与可视化技术",
        "subject_id": "46",
        "assessment_method": "考察+项目",
        "type": "B"
    },
    {
        "name": "机器学习",
        "description": "统计学习方法与实践",
        "subject_id": "46",
        "assessment_method": "考察+项目",
        "type": "B"
    },
    {
        "name": "高级数据结构",
        "description": "复杂数据结构的设计与应用",
        "subject_id": "46",
        "assessment_method": "考试",
        "type": "A"
    }
]

def generate_course_id(subject_id, grade, course_type):
    """生成课程ID
    
    格式：前两位课程所属专业的专业号 + 2位年级号 + 随机3位数字代码 + 课程性质（1位，大写字母）
    """
    random_digits = ''.join(random.choices(string.digits, k=3))
    return f"{subject_id}{grade}{random_digits}{course_type}"

def generate_courses(num_courses=30, output_file="courses.xlsx"):
    """生成指定数量的课程信息并保存到Excel文件
    
    Args:
        num_courses: 要生成的课程数量
        output_file: 输出的Excel文件名
    """
    data = []
    
    # 当前年份的后两位作为年级号的基础
    current_year = datetime.datetime.now().year % 100
    
    # 可用的年级号（当前年份-3至当前年份）
    available_grades = [f"{current_year-i:02d}" for i in range(4)]
    
    # 使用已配置的课程信息生成多个课程记录
    for _ in range(num_courses):
        course_config = random.choice(courses_config)
        subject_id = course_config["subject_id"]
        grade = random.choice(available_grades)
        course_type = course_config["type"]
        
        # 确保ID不重复
        course_id = generate_course_id(subject_id, grade, course_type)
        
        # 设置开始和结束日期（下一学期）
        start_date = datetime.date(2025, 9, 1)
        end_date = datetime.date(2025, 12, 31)
        
        course = {
            "id": course_id,
            "subject_id": subject_id,
            "name": course_config["name"],
            "description": course_config["description"],
            "cover_image": "",  # 空白封面
            "start_date": start_date.strftime("%Y-%m-%d"),
            "end_date": end_date.strftime("%Y-%m-%d"),
            "assessment_method": course_config["assessment_method"],
            "type": course_type
        }
        data.append(course)
    
    # 创建DataFrame并保存到Excel
    df = pd.DataFrame(data)
    
    # 确保script目录下的data文件夹存在
    output_dir = Path(__file__).parent.parent / "data"
    output_dir.mkdir(exist_ok=True)
    
    output_path = output_dir / output_file
    df.to_excel(output_path, index=False)
    print(f"已生成课程Excel文件: {output_path}")
    
    # 返回生成的路径，方便在其他脚本中使用
    return output_path

if __name__ == "__main__":
    generate_courses(num_courses=30, output_file="courses.xlsx")
