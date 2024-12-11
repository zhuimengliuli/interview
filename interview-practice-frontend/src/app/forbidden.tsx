import { Button, Result } from "antd";

/**
 * 无权限访问页面
 */
const Forbidden = () => {
  return (
    <Result
      status={403}
      title="403"
      subTitle="您无权访问"
      extra={
        <Button type="primary" href="/">
          返回首页
        </Button>
      }
    />
  );
};

export default Forbidden;
