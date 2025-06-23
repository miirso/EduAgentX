#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""
生成课程信息的Excel文件
支持命令行参数输入生成数量
"""

import pandas as pd
import random
import string
import datetime
import argparse
import sys
from pathlib import Path
from faker import Faker

# 初始化Faker，使用中文locale
fake = Faker('zh_CN')

# 课程信息配置 - 计算机类课程
courses_config = [
    {
        "name": "数据结构与算法",
        "description": "学习基本数据结构（数组、链表、栈、队列、树、图等）和经典算法，培养编程思维和算法设计能力。",
        "subject_id": None,  # 默认空白，但要求integer类型
        "assessment_method": "考试+实验",
        "type": "A"  # 必修课
    },
    {
        "name": "计算机网络",
        "description": "学习计算机网络基本原理、TCP/IP协议栈、网络编程技术，理解现代网络通信机制。",
        "subject_id": None,
        "assessment_method": "考试+课设",
        "type": "A"
    },
    {
        "name": "操作系统",
        "description": "学习操作系统基本概念、进程管理、内存管理、文件系统等核心技术。",
        "subject_id": None,
        "assessment_method": "考试+实验",
        "type": "A"
    },
    {
        "name": "数据库系统原理",
        "description": "学习关系数据库理论、SQL语言、数据库设计、事务处理、并发控制等技术。",
        "subject_id": None,
        "assessment_method": "考试+课设",
        "type": "A"
    },
    {
        "name": "Java程序设计",
        "description": "学习Java语言基础、面向对象编程、异常处理、集合框架、多线程编程等。",
        "subject_id": None,
        "assessment_method": "考查+实验",
        "type": "B"  # 选修课
    },
    {
        "name": "Python程序设计",
        "description": "学习Python语言基础、数据处理、Web开发、机器学习库的使用等。",
        "subject_id": None,
        "assessment_method": "考查+实验",
        "type": "B"
    },
    {
        "name": "软件工程",
        "description": "学习软件开发生命周期、需求分析、系统设计、测试技术、项目管理等。",
        "subject_id": None,
        "assessment_method": "考试+课设",
        "type": "A"
    },
    {
        "name": "Web开发技术",
        "description": "学习HTML5、CSS3、JavaScript、前端框架、后端开发、数据库集成等Web技术。",
        "subject_id": None,
        "assessment_method": "考查+课设",
        "type": "B"
    },
    {
        "name": "人工智能基础",
        "description": "学习机器学习算法、深度学习、神经网络、自然语言处理等AI核心技术。",
        "subject_id": None,
        "assessment_method": "考试+实验",
        "type": "A"
    },
    {
        "name": "计算机图形学",
        "description": "学习2D/3D图形渲染、图像处理、计算机视觉、OpenGL编程等技术。",
        "subject_id": None,
        "assessment_method": "考查+课设",
        "type": "B"
    },
    {
        "name": "编译原理",
        "description": "学习程序语言的编译过程、词法分析、语法分析、语义分析、代码生成等技术。",
        "subject_id": None,
        "assessment_method": "考试+实验",
        "type": "A"
    },
    {
        "name": "算法分析与设计",
        "description": "深入学习算法设计技巧、复杂度分析、动态规划、贪心算法、分治算法等。",
        "subject_id": None,
        "assessment_method": "考试+实验",
        "type": "A"
    },
    {
        "name": "移动应用开发",
        "description": "学习Android/iOS移动应用开发、跨平台开发框架、移动UI设计等技术。",
        "subject_id": None,
        "assessment_method": "考查+课设",
        "type": "B"
    },
    {
        "name": "信息安全",
        "description": "学习密码学基础、网络安全、系统安全、安全协议、渗透测试等技术。",
        "subject_id": None,
        "assessment_method": "考试+实验",
        "type": "A"
    },
    {
        "name": "大数据技术",
        "description": "学习Hadoop、Spark、数据挖掘、数据仓库、分布式计算等大数据处理技术。",
        "subject_id": None,
        "assessment_method": "考查+课设",
        "type": "B"
    },
    {
        "name": "云计算技术",
        "description": "学习虚拟化技术、容器技术、微服务架构、云平台服务等云计算核心技术。",
        "subject_id": None,
        "assessment_method": "考查+课设",
        "type": "B"
    },
    {
        "name": "计算机组成原理",
        "description": "学习计算机硬件结构、CPU设计、存储系统、输入输出系统等底层原理。",
        "subject_id": None,
        "assessment_method": "考试+实验",
        "type": "A"
    },
    {
        "name": "离散数学",
        "description": "学习集合论、逻辑代数、图论、组合数学等计算机科学的数学基础。",
        "subject_id": None,
        "assessment_method": "考试",
        "type": "A"
    },
    {
        "name": "数字图像处理",
        "description": "学习图像增强、图像分割、特征提取、模式识别等数字图像处理技术。",
        "subject_id": None,
        "assessment_method": "考查+实验",
        "type": "B"
    },
    {
        "name": "分布式系统",
        "description": "学习分布式系统架构、一致性协议、负载均衡、微服务等分布式技术。",
        "subject_id": None,
        "assessment_method": "考试+课设",
        "type": "A"
    }
]

def generate_course_id(index, course_type):
    """生成课程ID
    
    格式：年份(2位) + 序号(5位) + 类型(1位)
    例如：25000001A
    """
    current_year = datetime.datetime.now().year % 100  # 取年份后两位
    return f"{current_year:02d}{index+1:05d}{course_type}"

def generate_random_date_range():
    """生成随机的课程开始和结束日期（PostgreSQL date格式）"""
    current_year = datetime.datetime.now().year
    
    # 随机选择学期：1-春季学期(2-6月)，2-秋季学期(9-12月)
    semester = random.choice([1, 2])
    
    if semester == 1:  # 春季学期
        start_month = random.randint(2, 3)
        start_day = random.randint(1, 28)
        start_date = datetime.date(current_year, start_month, start_day)
        # 结束日期在开始日期后3-4个月
        end_date = start_date + datetime.timedelta(days=random.randint(90, 120))
    else:  # 秋季学期
        start_month = random.randint(9, 10)
        start_day = random.randint(1, 28)
        start_date = datetime.date(current_year, start_month, start_day)
        # 结束日期在开始日期后3-4个月
        end_date = start_date + datetime.timedelta(days=random.randint(90, 120))
        
        # 如果结束日期跨年了，调整到下一年
        if end_date.year > current_year:
            end_date = datetime.date(current_year + 1, end_date.month, end_date.day)
    
    return start_date, end_date

def generate_cover_image_url(course_name):
    """生成课程封面图片URL"""
    safe_name = course_name.replace(' ', '_').replace('/', '_')
    return f"https://edu-images.example.com/courses/{safe_name}.jpg"

def generate_courses(num_courses, output_file="courses.xlsx"):
    """生成指定数量的课程信息并保存到Excel文件
    
    Args:
        num_courses: 要生成的课程数量
        output_file: 输出的Excel文件名
    """
    print(f"=== 开始生成 {num_courses} 条课程数据 ===")
    
    data = []
    
    for i in range(num_courses):
        # 从预定义课程中循环选择
        course_config = courses_config[i % len(courses_config)]
        
        # 如果是重复的课程，在名称后添加编号
        course_name = course_config["name"]
        if i >= len(courses_config):
            course_name += f"({i // len(courses_config) + 1})"
        
        # 生成课程ID
        course_id = generate_course_id(i, course_config["type"])
        
        # 生成随机日期范围
        start_date, end_date = generate_random_date_range()
        
        course = {
            "id": course_id,
            "subject_id": course_config["subject_id"],  # None值，但列类型为integer
            "name": course_name,
            "description": course_config["description"],
            "cover_image": generate_cover_image_url(course_name),
            "start_date": start_date,  # 直接使用date对象，pandas会正确格式化
            "end_date": end_date,
            "assessment_method": course_config["assessment_method"],
            "type": course_config["type"]
        }
        data.append(course)
        
        # 进度提示
        if (i + 1) % 10 == 0 or (i + 1) == num_courses:
            print(f"已生成 {i + 1}/{num_courses} 条课程数据")
    
    # 创建DataFrame
    df = pd.DataFrame(data)
    
    # 确保subject_id列为整数类型（允许空值）
    df['subject_id'] = df['subject_id'].astype('Int64')
    
    # 确保script目录下的data文件夹存在
    output_dir = Path(__file__).parent.parent / "data" 
    output_dir.mkdir(exist_ok=True)
    
    output_path = output_dir / output_file
    
    # 使用ExcelWriter来设置更好的格式
    with pd.ExcelWriter(output_path, engine='openpyxl') as writer:
        df.to_excel(writer, sheet_name='courses', index=False)
        
        # 获取工作表并设置列宽
        worksheet = writer.sheets['courses']
        column_widths = {
            'A': 12,  # id
            'B': 12,  # subject_id  
            'C': 30,  # name
            'D': 50,  # description
            'E': 40,  # cover_image
            'F': 15,  # start_date
            'G': 15,  # end_date
            'H': 20,  # assessment_method
            'I': 8    # type
        }
        
        for col, width in column_widths.items():
            worksheet.column_dimensions[col].width = width
    
    print(f"=== 课程数据生成完成 ===")
    print(f"文件保存位置: {output_path}")
    print(f"共生成 {len(data)} 条课程记录")
    
    # 显示数据预览
    print("\n数据预览:")
    print("-" * 80)
    preview_df = df.head(3)
    print(preview_df.to_string(index=False, max_colwidth=30))
    
    return output_path

def main():
    """主函数 - 支持命令行参数"""
    print("=== 课程信息生成脚本 ===")
    
    # 解析命令行参数
    parser = argparse.ArgumentParser(description='生成计算机类课程基本信息Excel文件')
    parser.add_argument('count', type=int, help='要生成的课程数量')
    parser.add_argument('-o', '--output', default='courses_data.xlsx', 
                       help='输出Excel文件名 (默认: courses_data.xlsx)')
    
    args = parser.parse_args()
    
    # 验证参数
    if args.count <= 0:
        print("❌ 错误: 课程数量必须大于0")
        sys.exit(1)
    
    if args.count > 500:
        print("⚠️  警告: 生成数量较大，可能需要较长时间")
        confirm = input("是否继续? (y/n): ")
        if confirm.lower() != 'y':
            print("操作已取消")
            sys.exit(0)
    
    try:
        # 生成课程数据
        output_path = generate_courses(args.count, args.output)
        
        print(f"\n✅ 生成完成!")
        print(f"📁 文件位置: {output_path}")
        print(f"📊 课程数量: {args.count}")
        
    except Exception as e:
        print(f"❌ 生成失败: {str(e)}")
        sys.exit(1)

if __name__ == "__main__":
    main()
