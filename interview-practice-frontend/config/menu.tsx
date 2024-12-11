import { MenuDataItem } from "@umijs/route-utils";
import { CrownOutlined } from "@ant-design/icons";
import ACCESS_ENUM from "@/access/accessEnum";

// 菜单列表
export const menus = [
  {
    path: "/questions",
    name: "题目",
    access: ACCESS_ENUM.NOT_LOGIN,
  },
  {
    path: "/banks",
    name: "题库",
    access: ACCESS_ENUM.NOT_LOGIN,
  },
  {
    path: "/",
    name: "主页",
    access: ACCESS_ENUM.NOT_LOGIN,
  },
  {
    path: "/user",
    name: "用户",
    access: ACCESS_ENUM.NOT_LOGIN,
    children: [
      {
        path: "/user/center",
        name: "用户中心",
        access: ACCESS_ENUM.USER,
      },
    ],
  },
  {
    path: "/admin",
    name: "管理",
    icon: <CrownOutlined />,
    access: ACCESS_ENUM.ADMIN,
    children: [
      {
        path: "/admin/user",
        name: "用户管理",
        access: ACCESS_ENUM.ADMIN,
      },
      {
        path: "/admin/bank",
        name: "题库管理",
        access: ACCESS_ENUM.ADMIN,
      },
      {
        path: "/admin/question",
        name: "题目管理",
        access: ACCESS_ENUM.ADMIN,
      },
    ],
  },
] as MenuDataItem[];

export const findAllMenuItemByPath = (path: String): MenuDataItem | null => {
  return findMenuItemByPath(menus, path);
};

export const findMenuItemByPath = (
  menus: MenuDataItem[],
  path: String,
): MenuDataItem | null => {
  for (const menu of menus) {
    if (menu.path === path) {
      return menu;
    }
    if (menu.children) {
      const childrenMenuItem = findMenuItemByPath(menu.children, path);
      if (childrenMenuItem) {
        return childrenMenuItem;
      }
    }
  }
  return null;
};
