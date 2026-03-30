from playwright.sync_api import sync_playwright, TimeoutError as PlaywrightTimeoutError
import json
import os

# 配置信息（请替换为你的实际数据）
API_TOKEN = "ae2fc5d5-54cd-4365-8b13-b3de8d963fc5"
PROJECT_ID = "你的项目ID"  # CurseForge项目ID（从项目 overview 页面URL获取）
UPLOAD_FILE_PATH = "path/to/your/mod.jar"  # 本地文件路径（绝对路径或相对路径）
GAME_VERSIONS = [157, 158]  # 从版本列表中获取的游戏版本ID（可通过下方函数获取）

# 游戏版本API地址（用于获取支持的版本ID）
VERSIONS_API_URL = "https://minecraft.curseforge.com/api/game/versions"
# 文件上传API地址
UPLOAD_API_URL = f"https://minecraft.curseforge.com/api/projects/{PROJECT_ID}/upload-file"


def get_supported_game_versions():
    """获取CurseForge支持的游戏版本列表（用于选择上传时的gameVersions）"""
    with sync_playwright() as p:
        browser = p.chromium.launch(
            headless=False,
            args=["--no-sandbox", "--disable-blink-features=AutomationControlled"]
        )
        page = browser.new_page(
            user_agent="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"
        )

        # 隐藏自动化特征
        page.add_init_script("""
            Object.defineProperty(navigator, 'webdriver', { get: () => undefined })
        """)

        # 设置请求头（带API令牌）
        page.set_extra_http_headers({
            "X-Api-Token": API_TOKEN,
            "Accept": "application/json"
        })

        try:
            page.goto(VERSIONS_API_URL, wait_until="networkidle", timeout=30000)

            # 提取<pre>标签内的JSON数据（CurseForge返回格式）
            pre_element = page.query_selector("pre")
            if pre_element:
                json_content = pre_element.text_content().strip()
                return json.loads(json_content)  # 返回完整版本列表
            else:
                print("未找到版本数据，可能需要手动验证")
                page.wait_for_timeout(30000)  # 等待手动验证
                pre_element = page.query_selector("pre")
                if pre_element:
                    return json.loads(pre_element.text_content().strip())
                else:
                    print("获取版本失败")
                    return []
        except Exception as e:
            print(f"获取版本出错：{str(e)}")
            return []
        finally:
            browser.close()


def upload_mod_to_curseforge():
    """上传文件到CurseForge项目"""
    # 验证文件是否存在
    if not os.path.exists(UPLOAD_FILE_PATH):
        print(f"错误：文件不存在 - {UPLOAD_FILE_PATH}")
        return

    # 构建metadata JSON（上传配置）
    metadata = {
        "changelog": "自动上传测试：修复了XX问题，优化了性能",  # 更新日志
        "changelogType": "markdown",  # 日志格式：text/html/markdown
        "displayName": "我的Mod v1.0.0",  # 显示名称（可选）
        "gameVersions": GAME_VERSIONS,  # 支持的游戏版本ID（从版本列表获取）
        "releaseType": "beta",  # 发布类型：alpha/beta/release
        "isMarkedForManualRelease": False  # 是否手动发布（False则自动发布）
        # 可选：添加依赖关系
        # "relations": {
        #     "projects": [
        #         {"slug": "forge", "type": "requiredDependency"}  # 依赖Forge
        #     ]
        # }
    }

    with sync_playwright() as p:
        browser = p.chromium.launch(
            headless=False,
            args=["--no-sandbox", "--disable-blink-features=AutomationControlled"]
        )
        page = browser.new_page(
            user_agent="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"
        )

        # 隐藏自动化特征
        page.add_init_script("""
            Object.defineProperty(navigator, 'webdriver', { get: () => undefined })
        """)

        # 设置请求头（带API令牌）
        page.set_extra_http_headers({
            "X-Api-Token": API_TOKEN,
            "Accept": "application/json"
        })

        try:
            # 准备FormData（multipart/form-data格式）
            # 使用Playwright的请求拦截器发送POST请求
            response = page.request.post(
                UPLOAD_API_URL,
                multipart={
                    # metadata字段：JSON字符串
                    "metadata": json.dumps(metadata),
                    # file字段：本地文件（自动处理文件名和MIME类型）
                    "file": page.request.new_file_chooser(UPLOAD_FILE_PATH)
                },
                timeout=60000  # 上传超时时间（60秒）
            )

            # 解析响应
            if response.ok:
                result = response.json()
                print(f"上传成功！文件ID：{result['id']}")
                print(f"可在CurseForge项目页面查看：https://minecraft.curseforge.com/projects/{PROJECT_ID}/files/{result['id']}")
            else:
                print(f"上传失败，状态码：{response.status}")
                print("响应内容：", response.text()[:1000])

        except PlaywrightTimeoutError:
            print("上传超时（可能文件过大或网络慢）")
        except Exception as e:
            print(f"上传出错：{str(e)}")
        finally:
            browser.close()


if __name__ == "__main__":
    # 步骤1：获取支持的游戏版本（可选，用于确认GAME_VERSIONS是否正确）
    print("=== 获取支持的游戏版本 ===")
    versions = get_supported_game_versions()
    if versions:
        print("前5个版本ID参考：")
        for v in versions[:5]:
            print(f"版本：{v['name']}，ID：{v['id']}")

    # 步骤2：上传文件（确保GAME_VERSIONS已正确配置）
    print("\n=== 开始上传文件 ===")
    upload_mod_to_curseforge()