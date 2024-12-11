"use client";
import {LockOutlined, UserOutlined} from "@ant-design/icons";
import {LoginForm, ProFormText} from "@ant-design/pro-components";
import {Image, message} from "antd";
import Link from "next/link";
import {useDispatch} from "react-redux";
import {AppDispatch} from "../../../stores";
import {ProForm} from "@ant-design/pro-form/lib";
import {userLoginUsingPost} from "@/api/userController";
import {setLoginUser} from "@/stores/loginUser";
import {useRouter} from "next/navigation";

/**
 * 用户登录界面
 * @constructor
 */
const UserLoginPage: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const router = useRouter();
  const [form] = ProForm.useForm();
  /**
   * 提交
   * @param values
   */
  const doSubmit = async (values: API.UserLoginRequest) => {
    try {
      const res = await userLoginUsingPost(values);
      if (res.data) {
        message.success("登录成功");
        // 保存用户登录状态
        dispatch(setLoginUser(res.data));
        router.replace("/");
        form.resetFields();
      }
    } catch (e: any) {
      message.error("登录失败：" + e.message);
    }
  };
  return (
    <div id="userLoginPage">
      <LoginForm
        form={form}
        logo={
          <Image src="/assets/logo.png" alt="面试刷题" height={44} width={44} />
        }
        title="面试刷题 - 用户登录"
        subTitle="刷题网站"
        onFinish={doSubmit}
      >
        <ProFormText
          name="userAccount"
          fieldProps={{
            size: "large",
            prefix: <UserOutlined />,
          }}
          placeholder={"请输入用户账号"}
          rules={[
            {
              required: true,
              message: "请输入用户名!",
            },
          ]}
        />
        <ProFormText.Password
          name="userPassword"
          fieldProps={{
            size: "large",
            prefix: <LockOutlined />,
          }}
          placeholder={"请输入用户密码"}
          rules={[
            {
              required: true,
              message: "请输入密码！",
            },
          ]}
        />
        <div
          style={{
            marginBlockEnd: 24,
            textAlign: "end",
          }}
        >
          还没有账号？
          <Link href={"/user/register"}>去注册</Link>
        </div>
      </LoginForm>
    </div>
  );
};

export default UserLoginPage;
