#include <iostream>
#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc.hpp>

#define DD 10


int main(int argc, char** argv) {
    cv::Mat image;
    image = cv::imread("/home/nolv/Pictures/123.png",1);
    if(! image.data )                              // Check for invalid input
    {
        std::cout <<  "Could not open or find the image" << std::endl ;
        return -1;
    }

    int widthStep = std::ceil(image.cols / 32.0);
    int heightStep = std::ceil(image.rows / 32.0);

    for( int i = 0; i < 32; ++i )
    {
        cv::line(image, {0, heightStep*i}, {image.cols, heightStep*i}, {0, 255, 0}, 1);
        cv::line(image, {widthStep*i, 0}, {widthStep*i, image.rows}, {0,255,0}, 1);
    }
    cv::imshow("asd", image);
    cv::waitKey(0);
    cv::imwrite("FirstLayer.png", image);

    widthStep = std::ceil(image.cols / DD);
    heightStep = std::ceil(image.rows / DD);

    for( int i = 0; i < 32; ++i )
    {
        cv::line(image, {0, heightStep*i}, {image.cols, heightStep*i}, {0, 0, 255}, 2);
        cv::line(image, {widthStep*i, 0}, {widthStep*i, image.rows}, {0,0,255}, 2);
    }
    cv::imshow("asd", image);
    cv::waitKey(0);
    cv::imwrite("SecondLayer.png", image);
    return 0;
}