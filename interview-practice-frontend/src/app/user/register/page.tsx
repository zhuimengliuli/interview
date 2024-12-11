"use client";
import { LockOutlined, UserOutlined } from "@ant-design/icons";
import { LoginForm, ProFormText } from "@ant-design/pro-components";
import { Image, message } from "antd";
import Link from "next/link";
import { useDispatch } from "react-redux";
import { AppDispatch } from "../../../stores";
import { ProForm } from "@ant-design/pro-form/lib";
import { userRegisterUsingPost } from "@/api/userController";
import { useRouter } from "next/navigation";

/**
 * 用户注册界面
 * @constructor
 */
const UserRegisterPage: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const router = useRouter();
  const [form] = ProForm.useForm();
  /**
   * 提交
   * @param values
   */
  const doSubmit = async (values: API.UserRegisterRequest) => {
    try {
      const res = await userRegisterUsingPost(values);
      if (res.data) {
        message.success("注册成功，请登录");
        router.replace("/user/login");
        form.resetFields();
      }
    } catch (e: any) {
      message.error("注册失败：" + e.message);
    }
  };
  return (
    <div id="UserRegisterPage">
      <LoginForm
        form={form}
        logo={
          <Image src="/assets/logo.png" alt="面试刷题" height={44} width={44} />
        }
        title="面试刷题 - 用户注册"
        subTitle="刷题网站"
        onFinish={doSubmit}
        submitter={{
          searchConfig: {
            submitText: "注册",
          },
        }}
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
        <ProFormText.Password
          name="checkPassword"
          fieldProps={{
            size: "large",
            prefix: <LockOutlined />,
          }}
          placeholder={"请输入确认密码"}
          rules={[
            {
              required: true,
              message: "请输入确认密码！",
            },
          ]}
        />
        <div
          style={{
            marginBlockEnd: 24,
            textAlign: "end",
          }}
        >
          有账号？
          <Link href={"/user/login"}>去登录</Link>
        </div>
      </LoginForm>
    </div>
  );
};

export default UserRegisterPage;
